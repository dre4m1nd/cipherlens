package com.dre4m1nd.cipherlens.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.dre4m1nd.cipherlens.common.constants.ResultConstants.*;

@Schema(description = "统一返回结果类")
@Data
public class Result<T> {
    @Schema(description = "返回结果码")
    private Integer code;
    @Schema(description = "返回结果信息")
    private String message;
    @Schema(description = "返回结果数据")
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMessage(SUCCESS_MSG);
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMessage(SUCCESS_MSG);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(Integer code) {
        return error(code,ERROR_MSG);
    }

    public static <T> Result<T> error(Integer code,String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
