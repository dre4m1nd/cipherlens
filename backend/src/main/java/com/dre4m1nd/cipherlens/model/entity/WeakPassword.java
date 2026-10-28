package com.dre4m1nd.cipherlens.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dre4m1nd
 */
@Schema(description = "弱密码表")
@TableName(value ="weak_password")
@Data
public class WeakPassword {

    @Schema(description = "主键Id, 自增")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "弱密码")
    private String password;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}