package com.dre4m1nd.cipherlens.controller;

import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.model.vo.PasswordEvaluationLogVO;
import com.dre4m1nd.cipherlens.service.PasswordEvaluationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * @author dre4m1nd
 */
@Tag(name = "密码评估接口")
@Slf4j
@RestController
@RequestMapping("/evaluate")
public class EvaluateController {

    @Resource
    private PasswordEvaluationLogService passwordEvaluationLogService;

    @Operation(summary = "密码评估接口")
    @GetMapping()
    public Result<PasswordEvaluationLogVO> evaluate(@RequestParam String password) {
        log.info("开始评估:");
        return passwordEvaluationLogService.evaluate(password);
    }
    @Operation(summary = "获取评估结果接口")
    @GetMapping("/item/{passwordItemId}")
    public Result<PasswordEvaluationLogVO> getEvaluationLogById(@PathVariable Long passwordItemId) {
        log.info("开始评估获取评估结果:");
        return passwordEvaluationLogService.getEvaluationLogById(passwordItemId);
    }
}
