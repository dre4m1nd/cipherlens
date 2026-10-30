package com.dre4m1nd.cipherlens.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dre4m1nd.cipherlens.aop.annotation.OperationLogRecord;
import com.dre4m1nd.cipherlens.common.Enum.OperationContentEnum;
import com.dre4m1nd.cipherlens.common.Enum.OperationTypeEnum;
import com.dre4m1nd.cipherlens.common.crypto.AES256GCMUtil;
import com.dre4m1nd.cipherlens.common.crypto.PBKDF2Util;
import com.dre4m1nd.cipherlens.common.crypto.RSA4096Util;
import com.dre4m1nd.cipherlens.common.model.*;
import com.dre4m1nd.cipherlens.common.util.RedisTokenUtil;
import com.dre4m1nd.cipherlens.common.util.UserHolderUtil;
import com.dre4m1nd.cipherlens.common.util.VerificationCodeService;
import com.dre4m1nd.cipherlens.exception.BusinessException;
import com.dre4m1nd.cipherlens.exception.SystemException;
import com.dre4m1nd.cipherlens.exception.UnAuthorizedException;
import com.dre4m1nd.cipherlens.mapper.SysUserMapper;
import com.dre4m1nd.cipherlens.model.dto.SysUserDTO;
import com.dre4m1nd.cipherlens.model.entity.SysUser;
import com.dre4m1nd.cipherlens.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
* @author dre4m1nd
* @description 针对表【sys_user(用户信息表)】的数据库操作Service实现
* @createDate 2025-12-05 12:41:54
*/
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{

    @Resource
    private RedisTokenUtil redisTokenUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private VerificationCodeService verificationCodeService;

    /** RSA 公钥（对应华为云 KMS 密钥对），用于加密用户 AES 密钥 */
    @Value("${crypto.rsa.public-key:}")
    private String rsaPublicKey;

    /**
     * 用户登录业务逻辑
     * 
     * @param loginForm 登录表单，包含用户名和密码
     * @return 登录结果，成功时返回包含token的Result对象
     * @throws BusinessException 用户不存在或密码错误时抛出
     */
    @Override
    @OperationLogRecord(operationType = OperationTypeEnum.LOGIN, operationContent = OperationContentEnum.USER)
    public Result<Token> login(LoginForm loginForm) {
        // 根据用户名查询用户信息
        SysUser sysUser = lambdaQuery().eq(SysUser::getUsername, loginForm.getUsername()).one();
        if (ObjectUtil.isNull(sysUser)) {
            log.warn("用户不存在: {}", loginForm.getUsername());
            throw new BusinessException("用户不存在");
        }
        // 获取用户密码哈希和盐值
        String userPasswordHash = sysUser.getUserPasswordHash();
        String salt = sysUser.getSalt();
        // 验证密码
        if (!PBKDF2Util.verifyPassword(loginForm.getPassword(), userPasswordHash, salt)) {
            log.warn("密码错误: {}", loginForm.getUsername());
            throw new BusinessException("密码错误");
        }
        // 设置当前用户ID到线程上下文
        UserHolderUtil.set(sysUser.getId());
        // 生成登录token
        String token = redisTokenUtil.buildToken(sysUser.getId());
        // 检查token是否存在，不存在则重新保存
        if (!redisTokenUtil.isExistToken(token)){
            // 将用户信息转换为DTO并保存到Redis
            redisTokenUtil.saveToken(token, BeanUtil.copyProperties(sysUser, SysUserDTO.class));
            return Result.success(new Token(token));
        }
        return Result.success(new Token(token));
    }

    /**
     * 用户登出业务逻辑
     * 
     * @return 登出结果
     */
    @Override
    @OperationLogRecord(operationType = OperationTypeEnum.LOGOUT, operationContent = OperationContentEnum.USER)
    public Result<?> logout() {
        // 从线程上下文获取当前用户ID并移除对应token
        redisTokenUtil.removeToken(UserHolderUtil.get());
        // 清除线程上下文的用户信息
        UserHolderUtil.remove();
        return Result.success("退出成功");
    }

    /**
     * 用户注册业务逻辑
     * 
     * @param registerForm 注册表单，包含用户名、邮箱和密码
     * @return 注册结果
     * @throws BusinessException 用户名或邮箱已存在时抛出
     * @throws SystemException 注册失败时抛出
     * @throws Exception 加密过程中可能抛出的异常
     */
    @Override
    public Result<?> addUser(RegisterForm registerForm) throws Exception {
        // 检查用户名是否已存在
        if (this.lambdaQuery().eq(SysUser::getUsername, registerForm.getUsername()).count() > 0) {
            throw new BusinessException("用户已存在");
        }
        // 检查邮箱是否已存在
        if (this.lambdaQuery().eq(SysUser::getEmail, registerForm.getEmail()).count() > 0) {
            throw new BusinessException("邮箱已存在");
        }
        // 创建用户对象
        SysUser sysUser = new SysUser();
        sysUser.setUsername(registerForm.getUsername());
        sysUser.setEmail(registerForm.getEmail());
        // 生成密码盐值
        sysUser.setSalt(PBKDF2Util.generateSalt());
        // 生成密码哈希
        sysUser.setUserPasswordHash(PBKDF2Util.generatePasswordHash(registerForm.getPassword(), sysUser.getSalt()));
        // 生成并加密AES密钥
        try {
            sysUser.setAesKeyRsaEncrypted(RSA4096Util.encrypt(AES256GCMUtil.generateAESKey(), rsaPublicKey));
        } catch (IllegalArgumentException e) {
            log.error("用户密钥加密失败: {}", e.getMessage());
            throw new SystemException("服务端加密配置缺失，暂时无法注册");
        }
        // 保存用户信息到数据库
        if (!this.save(sysUser)) {
            throw new SystemException("注册失败");
        }
        return Result.success("注册成功");
    }

    /**
     * 发送重置密码邮箱验证码
     *
     * @param resetCodeForm 用户名 + 邮箱
     * @return 统一提示（不暴露账号是否存在）
     */
    @Override
    public Result<?> sendResetCode(ResetCodeForm resetCodeForm) {
        SysUser sysUser = this.lambdaQuery()
                .eq(SysUser::getUsername, resetCodeForm.getUsername())
                .eq(SysUser::getEmail, resetCodeForm.getEmail())
                .one();
        if (ObjectUtil.isNull(sysUser)) {
            // 不区分“用户不存在”与“邮箱不匹配”，避免账号枚举
            log.warn("重置密码验证码请求：用户名与邮箱不匹配");
            return Result.success("如果账号与邮箱匹配，验证码已发送");
        }
        verificationCodeService.sendResetCode(sysUser.getUsername(), sysUser.getEmail());
        return Result.success("如果账号与邮箱匹配，验证码已发送");
    }

    /**
     * 重置用户密码业务逻辑（需邮箱验证码）
     *
     * @param resetPasswordForm 密码重置表单，包含用户名、邮箱、验证码和新密码
     * @return 密码重置结果
     */
    @Override
    @OperationLogRecord(operationType = OperationTypeEnum.UPDATE, operationContent = OperationContentEnum.USER)
    public Result<?> resetPassword(ResetPasswordForm resetPasswordForm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 根据用户名和邮箱查询用户
        SysUser sysUser = this.lambdaQuery().eq(SysUser::getUsername, resetPasswordForm.getUsername()).eq(SysUser::getEmail, resetPasswordForm.getEmail()).one();
        if (ObjectUtil.isNull(sysUser)) {
            // 与验证码错误返回同一提示，避免通过接口探测账号
            throw new BusinessException("验证码错误或已过期");
        }
        // 校验并消费邮箱验证码
        verificationCodeService.verifyAndConsume(sysUser.getUsername(), resetPasswordForm.getCode());
        // 生成新密码哈希并更新到数据库
        if (!this.lambdaUpdate().eq(SysUser::getId, sysUser.getId()).setSql("user_password_hash={0}", PBKDF2Util.generatePasswordHash(resetPasswordForm.getPassword(), sysUser.getSalt())).update()) {
            throw new SystemException("更新密码失败");
        }
        // 密码已变更，使旧登录态立即失效
        redisTokenUtil.removeToken(sysUser.getId());
        return Result.success("重置密码成功");
    }

    /**
     * 更新用户信息业务逻辑
     * 
     * @param updateUserForm 用户信息更新表单，包含用户名和新密码（可选）
     * @return 更新结果
     * @throws BusinessException 用户名已存在时抛出
     * @throws SystemException 更新失败时抛出
     * @throws NoSuchAlgorithmException 加密算法不存在时抛出
     * @throws InvalidKeySpecException 密钥规格无效时抛出
     */
    @Override
    @OperationLogRecord(operationType = OperationTypeEnum.UPDATE, operationContent = OperationContentEnum.USER)
    public Result<?> updateSysUser(UpdateUserForm updateUserForm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 获取当前用户ID
        Long userId = UserHolderUtil.get();
        String username = updateUserForm.getUsername();
        String password = updateUserForm.getPassword();

        // 更新用户名（如果提供）
        if (StrUtil.isNotBlank(username)){
            // 检查新用户名是否已被其他用户使用
            if (this.lambdaQuery().eq(SysUser::getUsername, username).ne(SysUser::getId, userId).count() > 0) {
                throw new BusinessException("用户已存在");
            }
            // 更新用户名到数据库
            if (!this.lambdaUpdate().eq(SysUser::getId, userId).set(SysUser::getUsername, username).update()) {
                throw new SystemException("更新用户名失败");
            }
        }
        
        // 更新密码（如果提供）
        if (StrUtil.isNotBlank(password)){
            // 盐值直接从数据库读取，不依赖 Redis 中的登录缓存
            SysUser current = this.getById(userId);
            if (ObjectUtil.isNull(current)) {
                throw new BusinessException("用户不存在");
            }
            // 生成新密码哈希并更新
            if (!this.lambdaUpdate().eq(SysUser::getId, userId).setSql("user_password_hash={0}", PBKDF2Util.generatePasswordHash(password,current.getSalt())).update()){
                throw new SystemException("更新密码失败");
            }
        }
        
        return Result.success("更新成功");
    }

    @Override
    @OperationLogRecord(operationType = OperationTypeEnum.QUERY, operationContent = OperationContentEnum.USER)
    public Result<UpdateUserForm> getUserInfo() {
        Long userId = UserHolderUtil.get();
        if (ObjectUtil.isNull(userId)) {
            throw new UnAuthorizedException("用户未登录");
        }
        SysUser sysUser = this.getById(userId);
        if (ObjectUtil.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }
        UpdateUserForm updateUserForm = new UpdateUserForm(sysUser.getUsername(), null);
        return Result.success(updateUserForm);
    }


}




