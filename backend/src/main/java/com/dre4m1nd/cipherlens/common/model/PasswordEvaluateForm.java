package com.dre4m1nd.cipherlens.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 密码评估请求表单（用请求体传密码，避免密码出现在 URL / 访问日志中）
 *
 * @author dre4m1nd
 */
@Data
@Schema(description = "密码评估表单")
public class PasswordEvaluateForm {

    @Schema(description = "待评估密码")
    private String password;
}
