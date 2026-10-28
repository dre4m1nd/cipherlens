package com.dre4m1nd.cipherlens.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "更新用户表单")
@AllArgsConstructor
@Data
public class UpdateUserForm {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
}
