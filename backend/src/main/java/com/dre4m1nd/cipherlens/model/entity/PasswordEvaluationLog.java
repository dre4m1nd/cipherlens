package com.dre4m1nd.cipherlens.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dre4m1nd
 */
@Schema(description = "密码安全评估记录实体类")
@TableName(value ="password_evaluation_log")
@Data
public class PasswordEvaluationLog {

    @Schema(description = "主键Id, 自增")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "密码项ID")
    private Long passwordItemId;

    @Schema(description = "评估分数")
    private Integer evaluateScore;

    @Schema(description = "风险类型")
    private String riskType;

    @Schema(description = "建议")
    private String suggestions;

    @Schema(description = "评估时间")
    private LocalDateTime evaluateTime;

}