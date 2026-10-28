package com.dre4m1nd.cipherlens.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.model.entity.PasswordEvaluationLog;
import com.dre4m1nd.cipherlens.model.vo.PasswordEvaluationLogVO;

/**
* @author dre4m1nd
* @description 针对表【password_evaluation_log(密码安全评估记录表)】的数据库操作Service
* @createDate 2025-12-05 10:59:21
*/
public interface PasswordEvaluationLogService extends IService<PasswordEvaluationLog> {

    Result<PasswordEvaluationLogVO> addEvaluationLog(Long id, String password);

    PasswordEvaluationLog evaluateWithPasswordId(Long passwordItemId, String password);

    Result<PasswordEvaluationLogVO> evaluate(String password);

    Result<PasswordEvaluationLogVO> getEvaluationLogById(Long passwordItemId);
}
