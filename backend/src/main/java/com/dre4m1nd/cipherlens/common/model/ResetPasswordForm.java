package com.dre4m1nd.cipherlens.common.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "重置密码表单")
public class ResetPasswordForm {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "用户邮箱")
    private String email;
    @Schema(description = "用户密码")
    private String password;
}
