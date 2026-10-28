package com.dre4m1nd.cipherlens.common.util;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.dre4m1nd.cipherlens.exception.BusinessException;
import com.dre4m1nd.cipherlens.exception.SystemException;
import com.dre4m1nd.cipherlens.exception.UnAuthorizedException;
import com.dre4m1nd.cipherlens.model.dto.SysUserDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.dre4m1nd.cipherlens.common.constants.RedisConstants.TOKEN_KEY_PREFIX;
import static com.dre4m1nd.cipherlens.common.constants.RedisConstants.TOKEN_TTL;

/**
 * @author dre4m1nd
 */
@Slf4j
@Component
public class RedisTokenUtil {

    @Value("${token.secret-key}")
    private static String secretKey;

    private static final String HASH_SALT = "cipherlens-salt-2025";

    private static final String SEPARATOR = ",";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成Token
     * 
     * @param userId 用户ID
     * @return 生成的Token字符串，格式为Base64(userId,authHash)
     */
    public String buildToken(Long userId) {
        // 生成基于用户ID的HMAC-SHA256签名
        String authHash = hmacSha256WithSalt(userId);
        // 组合用户ID和签名，并用Base64编码
        return Base64.encode(userId + SEPARATOR + authHash, StandardCharsets.UTF_8);
    }

    /**
     * 保存Token到Redis
     * 
     * @param token 要保存的Token
     * @param sysUserDTO 用户信息DTO
     */
    public void saveToken(String token, SysUserDTO sysUserDTO) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        if (StrUtil.isBlank(tokenKey)) {
            return;
        }
        Map<String, Object> sysUserMap = BeanUtil.beanToMap(sysUserDTO, false, false);
        sysUserMap.forEach((key, value) -> {
            stringRedisTemplate.opsForHash().put(tokenKey, key, value.toString());
        });
        stringRedisTemplate.expire(tokenKey, TOKEN_TTL, TimeUnit.MINUTES);
    }

    /**
     * 检查Token是否存在于Redis中
     * 
     * @param token 要检查的Token
     * @return Token是否存在
     */
    public boolean isExistToken(String token) {
        if (StrUtil.isBlank(token)) {
            throw new UnAuthorizedException("未登录");
        }
        String tokenKey = TOKEN_KEY_PREFIX + token;
        Long isExpired = stringRedisTemplate.getExpire(tokenKey);
        return isExpired != -2;
    }

    /**
     * 验证Token的有效性
     * 
     * @param token 待验证的Token
     * @return Token是否有效
     */
    public boolean isValidToken(String token) {
        // Token为空直接返回无效
        if (StrUtil.isBlank(token)) {
            throw new UnAuthorizedException("未登录");
        }
        // 解码Token并按分隔符分割
        String[] strings = Base64.decodeStr(token, StandardCharsets.UTF_8).split(SEPARATOR);
        try {
            // 验证签名是否匹配：重新计算签名并与Token中的签名比较
            if (!strings[1].equals(hmacSha256WithSalt(Long.parseLong(strings[0])))){
                throw new UnAuthorizedException("签名验证失败");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("无效token格式: {}", e.getMessage());
            throw new UnAuthorizedException("无效token格式");
        }
        if (!isExistToken(token)) {
            log.error("token已过期");
            throw new UnAuthorizedException("token已过期");
        }
        return true;
    }


    public void refreshToken(String token) {
        if (StrUtil.isBlank(token)) {
            return;
        }
        String tokenKey = TOKEN_KEY_PREFIX + token;
        if (!isExistToken(token)) {
            return;
        }
        stringRedisTemplate.expire(tokenKey, TOKEN_TTL, TimeUnit.MINUTES);
    }

    public Long getUserId(String token){
        if (StrUtil.isBlank(token)) {
            throw new UnAuthorizedException("未登录");
        }
        String[] strings = null;
        try {
            strings = Base64.decodeStr(token, StandardCharsets.UTF_8).split(SEPARATOR);
        } catch (Exception e) {
            throw new UnAuthorizedException("token解析失败");
        }
        return Long.parseLong(strings[0]);
    }

    public <T> T getFieldByToken(String token, Function<SysUserDTO, T> function) {
        if (StrUtil.isBlank(token)) {
            return null;
        }
        String tokenKey = TOKEN_KEY_PREFIX + token;
        SysUserDTO sysUserDTO = null;
        try {
            sysUserDTO = BeanUtil.mapToBean(stringRedisTemplate.opsForHash().entries(tokenKey), SysUserDTO.class, false);
        } catch (Exception e) {
            throw new SystemException("内部解析异常");
        }
        return function.apply(sysUserDTO);
    }

    /**
     * 根据用户ID移除Token
     * 
     * @param userId 用户ID
     */
    public void removeToken(Long userId) {
        if (userId == null) {
            return;
        }
        String tokenKey = TOKEN_KEY_PREFIX + buildToken(userId);;
        stringRedisTemplate.delete(tokenKey);
    }

    /**
     * 使用HMAC-SHA256算法和盐值生成签名
     * 
     * @param userId 用户ID
     * @return 十六进制格式的签名结果
     */
    private String hmacSha256WithSalt(Long userId) {
        // 组合盐值和用户ID作为待签名数据
        String factor = HASH_SALT + userId;
        // 创建HMAC-SHA256实例，使用配置的密钥
        HMac hmacSha256 = new HMac(HmacAlgorithm.HmacSHA256, secretKey.getBytes());
        // 生成消息摘要
        byte[] digest = hmacSha256.digest(factor.getBytes());
        // 转换为十六进制字符串返回
        return HexUtil.encodeHexStr(digest);
    }


}
