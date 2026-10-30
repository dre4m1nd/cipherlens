package com.dre4m1nd.cipherlens.common.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "重置密码表单")
public class ResetPasswordForm {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "用户邮箱")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "邮箱验证码")
    private String code;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 64, message = "密码长度需在 8~64 位之间")
    @Schema(description = "用户密码")
    private String password;
}
