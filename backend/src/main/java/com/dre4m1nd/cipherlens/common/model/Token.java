package com.dre4m1nd.cipherlens.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dre4m1nd
 */
@Schema(description = "Token")
@Data
@AllArgsConstructor
public class Token {
    @Schema(description = "token")
    String token;
}
