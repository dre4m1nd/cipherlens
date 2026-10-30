package com.dre4m1nd.cipherlens.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dre4m1nd.cipherlens.aop.annotation.OperationLogRecord;
import com.dre4m1nd.cipherlens.common.Enum.OperationContentEnum;
import com.dre4m1nd.cipherlens.common.crypto.AES256GCMUtil;
import com.dre4m1nd.cipherlens.common.Enum.OperationTypeEnum;
import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.common.util.HuaweiKmsUtil;
import com.dre4m1nd.cipherlens.common.util.UserHolderUtil;
import com.dre4m1nd.cipherlens.exception.BusinessException;
import com.dre4m1nd.cipherlens.exception.SystemException;
import com.dre4m1nd.cipherlens.exception.UnAuthorizedException;
import com.dre4m1nd.cipherlens.mapper.PasswordItemMapper;
import com.dre4m1nd.cipherlens.model.dto.PageQueryDTO;
import com.dre4m1nd.cipherlens.model.dto.PasswordItemDTO;
import com.dre4m1nd.cipherlens.model.dto.PasswordItemQueryDTO;
import com.dre4m1nd.cipherlens.model.entity.PasswordEvaluationLog;
import com.dre4m1nd.cipherlens.model.entity.PasswordItem;
import com.dre4m1nd.cipherlens.model.entity.SysUser;
import com.dre4m1nd.cipherlens.model.vo.PasswordItemVO;
import com.dre4m1nd.cipherlens.service.PasswordEvaluationLogService;
import com.dre4m1nd.cipherlens.service.PasswordItemService;
import com.dre4m1nd.cipherlens.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.dre4m1nd.cipherlens.common.constants.RedisConstants.*;

/**
* @author dre4m1nd
* description  针对表【password_item(密码条目表)】的数据库操作Service实现
* createDate  2025-12-05 10:59:11
 */
@Slf4j
@Service
public class PasswordItemServiceImpl extends ServiceImpl<PasswordItemMapper, PasswordItem>
    implements PasswordItemService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private PasswordEvaluationLogService passwordEvaluationLogService;

    @Override
    @Transactional
    @OperationLogRecord(operationType = OperationTypeEnum.ADD, operationContent = OperationContentEnum.PASSWORD_ITEM)
    public Result<?> addPasswordItem(PasswordItemDTO passwordItemDTO) throws Exception {
        Long userId = UserHolderUtil.get();
        if (ObjectUtil.isNull(passwordItemDTO)) {
            throw new BusinessException("密码条目不能为空");
        }
        PasswordItem passwordItem = BeanUtil.copyProperties(passwordItemDTO, PasswordItem.class);
        passwordItem.setId(null);
        passwordItem.setUserId(userId);
        // 明文仅用于评估，落库前先加密
        String plainPassword = passwordItem.getPassword();
        passwordItem.setPassword(AES256GCMUtil.encrypt(plainPassword, this.getAesKey()));
        if (!this.save(passwordItem)) {
            throw new SystemException("添加密码条目失败");
        }
        if (!passwordEvaluationLogService.save(passwordEvaluationLogService.evaluateWithPasswordId(passwordItem.getId(), plainPassword))) {
            throw new SystemException("添加密码安全评估记录失败");
        }
        return Result.success();
    }

    @Transactional
    @Override
    @OperationLogRecord(operationType = OperationTypeEnum.DELETE, operationContent = OperationContentEnum.PASSWORD_ITEM)
    public Result<?> removePasswordItem(Long id) {
        if (id == null) {
            throw new BusinessException("密码条目ID不能为空");
        }
        // 带上 userId 条件，防止越权删除他人数据
        boolean removed = this.remove(new LambdaQueryWrapper<PasswordItem>()
                .eq(PasswordItem::getId, id)
                .eq(PasswordItem::getUserId, UserHolderUtil.get()));
        if (!removed) {
            throw new BusinessException("密码条目不存在");
        }
        passwordEvaluationLogService.remove(new LambdaQueryWrapper<>(PasswordEvaluationLog.class)
                .eq(PasswordEvaluationLog::getPasswordItemId, id));
        stringRedisTemplate.delete(this.cacheKey(id));
        return Result.success();
    }

    @Transactional
    @Override
    @OperationLogRecord(operationType = OperationTypeEnum.UPDATE, operationContent = OperationContentEnum.PASSWORD_ITEM)
    public Result<?> updatePasswordItem(Long id, PasswordItemDTO passwordItemDTO) {
        Long userId = UserHolderUtil.get();
        if (ObjectUtil.isNull(passwordItemDTO)) {
            throw new BusinessException("密码条目不能为空");
        }
        PasswordItem passwordItem = this.lambdaQuery().eq(PasswordItem::getId, id).eq(PasswordItem::getUserId, userId).one();
        if (ObjectUtil.isNull(passwordItem)) {
            throw new BusinessException("密码条目不存在");
        }
        // 密码单独处理：为空表示不修改，非空才加密
        String plainPassword = passwordItemDTO.getPassword();
        BeanUtil.copyProperties(passwordItemDTO, passwordItem,
                CopyOptions.create().ignoreNullValue()
                        .setIgnoreProperties("id", "userId", "password"));
        boolean passwordChanged = StrUtil.isNotBlank(plainPassword);
        if (passwordChanged) {
            try {
                passwordItem.setPassword(AES256GCMUtil.encrypt(plainPassword, this.getAesKey()));
            } catch (Exception e) {
                throw new SystemException("密码加密失败");
            }
        }
        if (!this.updateById(passwordItem)) {
            throw new SystemException("更新密码条目失败");
        }
        if (passwordChanged) {
            passwordEvaluationLogService.remove(new LambdaQueryWrapper<>(PasswordEvaluationLog.class)
                    .eq(PasswordEvaluationLog::getPasswordItemId, id));
            if (!passwordEvaluationLogService.save(passwordEvaluationLogService.evaluateWithPasswordId(id, plainPassword))) {
                throw new SystemException("添加密码安全评估记录失败");
            }
        }
        stringRedisTemplate.delete(this.cacheKey(id));
        return Result.success();
    }

    @Override
    public Result<PasswordItemVO> queryPasswordItemById(Long id) {
        if (ObjectUtil.isNull(id)){
            throw new BusinessException("密码条目ID不能为空");
        }
        // 缓存 key 必须带 userId，避免多用户共享同一条目缓存造成越权读取
        String cacheKey = this.cacheKey(id);
        String jsonStr = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isNotBlank(jsonStr)) {
            return Result.success(JSONUtil.toBean(jsonStr, PasswordItemVO.class));
        }
        if (jsonStr != null) {
            throw new BusinessException("密码条目不存在");
        }
        PasswordItem passwordItem = this.lambdaQuery().eq(PasswordItem::getId, id).eq(PasswordItem::getUserId, UserHolderUtil.get()).one();
        if (ObjectUtil.isNull(passwordItem)) {
            stringRedisTemplate.opsForValue().set(cacheKey, "", NULL_VALUE_TTL, TimeUnit.MINUTES);
            throw new BusinessException("密码条目不存在");
        }
        PasswordItemVO passwordItemVO = BeanUtil.copyProperties(passwordItem, PasswordItemVO.class);
        stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(passwordItemVO), PASSWORD_ITEM_TTL, TimeUnit.MINUTES);
        return Result.success(passwordItemVO);
    }

    @Override
    public Result<IPage<PasswordItemVO>> listPasswordItem(PageQueryDTO pageQueryDTO, PasswordItemQueryDTO passwordItemQueryDTO) {
        Page<PasswordItem> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());

        LambdaQueryWrapper<PasswordItem> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(PasswordItem::getUserId, UserHolderUtil.get());

        queryWrapper.like(StrUtil.isNotBlank(passwordItemQueryDTO.getAppName()),
                PasswordItem::getAppName, passwordItemQueryDTO.getAppName());

        queryWrapper.like(StrUtil.isNotBlank(passwordItemQueryDTO.getAccount()),
                PasswordItem::getAccount, passwordItemQueryDTO.getAccount());

        IPage<PasswordItem> passwordItemPage = this.page(page, queryWrapper);
        return Result.success(passwordItemPage.convert(passwordItem -> BeanUtil.copyProperties(passwordItem, PasswordItemVO.class)));

    }


    @OperationLogRecord(operationType = OperationTypeEnum.QUERY, operationContent = OperationContentEnum.PASSWORD_ITEM)
    @Override
    public Result<PasswordItemVO> getPlainText(Long id) throws Exception {
        if (id == null){
            throw new BusinessException("密码条目ID不能为空");
        }
        PasswordItem passwordItem = this.lambdaQuery().eq(PasswordItem::getId, id).eq(PasswordItem::getUserId, UserHolderUtil.get()).one();
        if (ObjectUtil.isNull(passwordItem)) {
            throw new BusinessException("密码条目不存在");
        }
        String decrypted = AES256GCMUtil.decrypt(passwordItem.getPassword(), this.getAesKey());
        passwordItem.setPassword(decrypted);
        return Result.success(BeanUtil.copyProperties(passwordItem, PasswordItemVO.class));
    }

    private String getAesKey() {
        Long userId = UserHolderUtil.get();
        if (userId == null) {
            throw new UnAuthorizedException("用户未登录");
        }
        String cacheKey = AES_KEY_PREFIX + userId;
        String key = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isNotBlank(key)) {
            return key;
        }
        SysUser sysUser = sysUserService.getById(userId);
        if (sysUser == null || StrUtil.isBlank(sysUser.getAesKeyRsaEncrypted())) {
            throw new SystemException("用户密钥不存在，请联系管理员");
        }
        key = HuaweiKmsUtil.decrypt(sysUser.getAesKeyRsaEncrypted());
        stringRedisTemplate.opsForValue().set(cacheKey, key, AES_KEY_TTL, TimeUnit.MINUTES);
        return key;
    }

    /**
     * 密码条目缓存 key（按用户隔离）
     */
    private String cacheKey(Long id) {
        return PASSWORD_ITEM_KEY_PREFIX + UserHolderUtil.get() + ":" + id;
    }
}




