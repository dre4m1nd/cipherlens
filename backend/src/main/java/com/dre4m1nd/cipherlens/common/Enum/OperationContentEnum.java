package com.dre4m1nd.cipherlens.common.Enum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author dre4m1nd
 */

@Getter
@Schema(description = "操作内容枚举")
public enum OperationContentEnum {

    USER("USER", "用户"),
    PASSWORD_ITEM("PASSWORD_ITEM", "密码条目"),
    OPERATION_LOG("OPERATION_LOG", "操作日志");


    private final String content;
    private final String description;

    OperationContentEnum(String content, String description) {
        this.content = content;
        this.description = description;
    }
}
