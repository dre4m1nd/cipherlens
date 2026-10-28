package com.dre4m1nd.cipherlens.model.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dre4m1nd
 */
@Data
public class OperationLogQueryDTO {

    @Schema(description = "操作类型")
    private String operationType;
    @Schema(description = "操作内容")
    private String operationContent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

}
