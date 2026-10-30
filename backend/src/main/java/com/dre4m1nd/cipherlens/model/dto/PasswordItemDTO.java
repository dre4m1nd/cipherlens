package com.dre4m1nd.cipherlens.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author dre4m1nd
 */
@Schema(description = "密码条目传输类")
@Data
public class PasswordItemDTO {

    @Schema(description = "密码条目ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @NotBlank(message = "应用名称不能为空")
    @Schema(description = "应用/网站名称")
    private String appName;

    @NotBlank(message = "登录账号不能为空")
    @Schema(description = "登录账号")
    private String account;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;

    @Schema(description = "密码条目备注")
    private String remark;

}