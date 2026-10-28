package com.dre4m1nd.cipherlens.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dre4m1nd
 */
@Schema(description = "密码条目可视化类")
@TableName(value ="password_item")
@Data
public class PasswordItemVO {

    @Schema(description = "密码条目ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "应用/网站名称")
    private String appName;

    @Schema(description = "登录账号")
    private String account;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "密码条目备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @Schema(description = "密码条目创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @Schema(description = "密码条目更新时间")
    private LocalDateTime updateTime;

}