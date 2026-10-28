package com.dre4m1nd.cipherlens.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "用户注册表单类")
@Data
public class RegisterForm {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "邮箱")
    private String email;

}
