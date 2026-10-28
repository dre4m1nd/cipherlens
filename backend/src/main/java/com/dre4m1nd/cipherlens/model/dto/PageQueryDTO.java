package com.dre4m1nd.cipherlens.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dre4m1nd
 */
@Data
@AllArgsConstructor
public class PageQueryDTO {
    @Min(value = 1, message = "页码不能小于1")
    @Schema(description = "当前页码")
    private Integer pageNum;
    @Min(value = 10, message = "每页条数不能小于10")
    @Max(value = 50, message = "每页条数不能大于50")
    @Schema(description = "每页条数")
    private Integer pageSize;
}
