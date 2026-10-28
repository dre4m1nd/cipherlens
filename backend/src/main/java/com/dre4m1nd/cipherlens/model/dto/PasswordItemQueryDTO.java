package com.dre4m1nd.cipherlens.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author dre4m1nd
 */
@Data
public class PasswordItemQueryDTO {
    @Schema(description = "应用名称")
    private String appName;
    @Schema(description = "账号")
    private String account;

}
