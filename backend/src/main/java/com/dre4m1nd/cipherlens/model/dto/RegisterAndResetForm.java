package com.dre4m1nd.cipherlens.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author dre4m1nd
 */
@Schema(description = "用户注册表单类")
@Data
public class RegisterAndResetForm {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
    @NotBlank(message = "邮箱不能为空")
    @Schema(description = "邮箱")
    private String email;

}
