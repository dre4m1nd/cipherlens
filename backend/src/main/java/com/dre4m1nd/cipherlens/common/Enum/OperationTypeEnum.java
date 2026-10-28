package com.dre4m1nd.cipherlens.common.Enum;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author dre4m1nd
 */

@Getter
@Schema(description = "操作类型枚举")
public enum OperationTypeEnum {

    LOGIN("LOGIN","登录"),

    LOGOUT("LOGOUT","登出"),

    ADD("ADD","添加"),

    DELETE("DELETE","删除"),

    UPDATE("UPDATE","更新"),

    QUERY("QUERY","查询"),

    EVALUATE("EVALUATE","评估");

    private final String operationType;

    private final String description;

    OperationTypeEnum(String operationType, String description) {
        this.operationType = operationType;
        this.description = description;
    }
}
