package com.dre4m1nd.cipherlens.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dre4m1nd
 */
@Schema(description = "操作日志可视化类")
@Data
public class OperationLogVO {

    @Schema(description = "操作日志ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 操作类型（登录/新增密码/修改密码/删除密码/评估密码）
     */
    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作内容")
    private String operationContent;

    @Schema(description = "操作结果")
    private Integer operationResult;

    @Schema(description = "耗时")
    private Long costTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

}