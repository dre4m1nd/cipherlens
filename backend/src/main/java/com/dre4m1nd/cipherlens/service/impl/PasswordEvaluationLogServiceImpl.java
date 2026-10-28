package com.dre4m1nd.cipherlens.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.common.util.SecurityEvaluationUtil;
import com.dre4m1nd.cipherlens.common.util.UserHolderUtil;
import com.dre4m1nd.cipherlens.exception.BusinessException;
import com.dre4m1nd.cipherlens.exception.UnAuthorizedException;
import com.dre4m1nd.cipherlens.mapper.PasswordEvaluationLogMapper;
import com.dre4m1nd.cipherlens.model.entity.PasswordEvaluationLog;
import com.dre4m1nd.cipherlens.model.vo.PasswordEvaluationLogVO;
import com.dre4m1nd.cipherlens.service.PasswordEvaluationLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
* @author dre4m1nd
* @description 针对表【password_evaluation_log(密码安全评估记录表)】的数据库操作Service实现
* @createDate 2025-12-05 10:59:21
*/
@Service
public class PasswordEvaluationLogServiceImpl extends ServiceImpl<PasswordEvaluationLogMapper, PasswordEvaluationLog>
    implements PasswordEvaluationLogService{

    @Resource
    private SecurityEvaluationUtil securityEvaluationUtil;

    @Override
    public Result<PasswordEvaluationLogVO> addEvaluationLog(Long passwordItemId, String password) {
        PasswordEvaluationLog passwordEvaluationLog = this.evaluateWithPasswordId(passwordItemId, password);
        PasswordEvaluationLogVO passwordEvaluationLogVO = BeanUtil.copyProperties(passwordEvaluationLog, PasswordEvaluationLogVO.class);
        return Result.success(passwordEvaluationLogVO);
    }

    @Override
    public PasswordEvaluationLog evaluateWithPasswordId(Long passwordItemId, String password) {
        if (passwordItemId == null) {
            throw new BusinessException("密码项ID不能为空");
        }
        if (password == null) {
            throw new BusinessException("密码不能为空");
        }
        Long userId = UserHolderUtil.get();
        if (userId == null) {
            throw new UnAuthorizedException("用户未登录");
        }
        return securityEvaluationUtil.evaluate(userId, passwordItemId, password);
    }



    @Override
    public Result<PasswordEvaluationLogVO> evaluate(String password) {
        if (password == null) {
            throw new BusinessException("密码不能为空");
        }
        PasswordEvaluationLog evaluate = securityEvaluationUtil.evaluate(null, null, password);
        PasswordEvaluationLogVO passwordEvaluationLogVO = BeanUtil.copyProperties(evaluate, PasswordEvaluationLogVO.class);
        return Result.success(passwordEvaluationLogVO);
    }

    @Override
    public Result<PasswordEvaluationLogVO> getEvaluationLogById(Long passwordItemId) {
        if (ObjectUtil.isNull(passwordItemId)) {
            throw new BusinessException("密码项ID不能为空");
        }
        PasswordEvaluationLog passwordEvaluationLog = this.lambdaQuery().eq(PasswordEvaluationLog::getPasswordItemId, passwordItemId).one();
        if (ObjectUtil.isNull(passwordEvaluationLog)) {
            throw new BusinessException("密码安全评估记录不存在");
        }
        PasswordEvaluationLogVO passwordEvaluationLogVO = BeanUtil.copyProperties(passwordEvaluationLog, PasswordEvaluationLogVO.class);
        return Result.success(passwordEvaluationLogVO);
    }


}




