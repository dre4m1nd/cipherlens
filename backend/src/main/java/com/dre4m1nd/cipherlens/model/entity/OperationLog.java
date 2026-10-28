package com.dre4m1nd.cipherlens.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "操作日志实体类")
@TableName(value ="operation_log")
@Data
public class OperationLog {

    @TableId(type = IdType.AUTO)
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

    @Schema(description = "操作时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime operationTime;

}