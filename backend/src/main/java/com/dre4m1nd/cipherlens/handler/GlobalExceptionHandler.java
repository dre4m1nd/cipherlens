package com.dre4m1nd.cipherlens.handler;


import cn.hutool.core.util.StrUtil;
import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.exception.BusinessException;
import com.dre4m1nd.cipherlens.exception.UnAuthorizedException;
import com.dre4m1nd.cipherlens.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.List;

/**
 * @author dre4m1nd
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数验证异常辅助方法
     * @param bindingResult 绑定结果
     * @return 格式化后的错误信息
     */
    private String buildValidationErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getField())
                    .append(" - ")
                    .append(fieldError.getDefaultMessage())
                    .append("；");
        }
        return StrUtil.isNotBlank(errorMessage.toString()) ? errorMessage.toString() : "参数验证异常";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = buildValidationErrorMessage(e.getBindingResult());
        log.warn("参数验证异常: {}", errorMessage);
        return Result.error(400, errorMessage);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        String errorMessage = buildValidationErrorMessage(e.getBindingResult());
        log.warn("参数验证异常: {}", errorMessage);
        return Result.error(400,errorMessage);
    }


    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleSQLException(SQLException e) {
        log.error("数据库操作异常: {}", e.getMessage(), e);
        // 不向客户端暴露 SQL / 堆栈细节
        return Result.error(500, "数据库异常，请稍后重试");
    }


    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage(),e);
        return Result.error(400,StrUtil.isNotBlank(e.getMessage())?e.getMessage():"业务异常");
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleNoUnAuthorizedException(UnAuthorizedException e) {
        log.warn("权限异常: {}", e.getMessage(),e);
        return Result.error(401,StrUtil.isNotBlank(e.getMessage())?e.getMessage():"没有权限");
    }


    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleSystemException(SystemException e) {
        log.error("系统异常: {}", e.getMessage(),e);
        return Result.error(500,StrUtil.isNotBlank(e.getMessage())?e.getMessage():"系统异常");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        // 未知异常统一返回笼统提示，避免泄露内部实现细节
        return Result.error(500, "系统异常，请稍后重试");
    }

}
