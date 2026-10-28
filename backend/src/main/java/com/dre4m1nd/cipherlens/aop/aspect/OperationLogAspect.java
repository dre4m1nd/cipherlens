package com.dre4m1nd.cipherlens.aop.aspect;


import cn.hutool.core.util.ObjectUtil;
import com.dre4m1nd.cipherlens.aop.annotation.OperationLogRecord;
import com.dre4m1nd.cipherlens.common.Enum.OperationContentEnum;
import com.dre4m1nd.cipherlens.common.Enum.OperationTypeEnum;
import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.common.util.UserHolderUtil;
import com.dre4m1nd.cipherlens.exception.SystemException;
import com.dre4m1nd.cipherlens.model.entity.OperationLog;
import com.dre4m1nd.cipherlens.service.OperationLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Resource
    private OperationLogService operationLogService;

    @Pointcut("@annotation(com.dre4m1nd.cipherlens.aop.annotation.OperationLogRecord)")
    public void operationLogPointCut(){}

    @Around("operationLogPointCut()")
    public Result<?> doAround(ProceedingJoinPoint joinPoint) {
        log.info("==========操作日志记录开始==========");
        // 初始化操作日志对象
        OperationLog operationLog = new OperationLog();
        // 记录方法开始执行时间（用于计算耗时）
        long startTime = System.currentTimeMillis();
        // 初始化返回结果
        Result<?> result = null;
        Long userId = null;

        try {
            // ========= 获取用户ID ===========
            if (UserHolderUtil.get() != null) {
                userId = UserHolderUtil.get();
            }

            // ========= 获取注解上的属性 ===========
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLogRecord operationLogRecord = method.getAnnotation(OperationLogRecord.class);
            OperationTypeEnum operationType = operationLogRecord.operationType();
            OperationContentEnum operationContent = operationLogRecord.operationContent();

            // ============ 基础属性提前设置 ==============
            operationLog.setOperationType(operationType.getOperationType());
            operationLog.setOperationContent(operationContent.getContent());
            // ============ 调用目标方法 ==============
            result = (Result<?>) joinPoint.proceed();

            // ============ 方法执行成功 - 设置属性 ============
            if (UserHolderUtil.get() != null) {
                userId = UserHolderUtil.get();
            }
            // 登录场景：成功后才有userId，无需抛异常
            operationLog.setUserId(ObjectUtil.isNull(userId) ? 0 : userId);
            operationLog.setOperationResult(1);
            Long costTime = System.currentTimeMillis() - startTime;
            operationLog.setCostTime(costTime);

            log.info("操作成功,用户Id:{},操作类型:{},操作目标:{},操作耗时:{}ms",
                    userId, operationType, operationContent, costTime);

        } catch (Throwable e) {
            // ============= 方法执行失败 - 设置属性 ============
            if (UserHolderUtil.get() != null){
                userId = UserHolderUtil.get();
            }
            // 失败场景：userId为空则设为0，避免数据库异常
            operationLog.setUserId(ObjectUtil.isNull(userId) ? 0 : userId);
            operationLog.setOperationResult(0);
            operationLog.setCostTime(System.currentTimeMillis() - startTime);
            if (!operationLogService.save(operationLog)) {
                throw new SystemException("操作日志保存失败");
            }
            log.error("操作失败,用户Id:{},操作类型:{},操作目标:{},异常信息:{}",
                    operationLog.getUserId(), operationLog.getOperationType(),
                    // 补充完整堆栈
                    operationLog.getOperationContent(), e.getMessage(), e);

            // 继续抛出异常，让全局异常处理器捕获
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e.getMessage(), e);
            }

        } finally {
            // ============ 最终保存日志（无论成功/失败） ============
            try {
                operationLogService.save(operationLog);
                // 若有自增ID可打印
                log.info("操作日志保存成功,日志ID:{}", operationLog.getId());
            } catch (Exception logE) {
                // 日志保存失败不影响主流程
                log.error("操作日志保存失败", logE);
            }
            log.info("==========操作日志记录结束==========");
        }
        return result;
    }


}
