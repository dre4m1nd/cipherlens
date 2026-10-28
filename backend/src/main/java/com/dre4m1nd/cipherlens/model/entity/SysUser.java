package com.dre4m1nd.cipherlens.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dre4m1nd
 */
@Schema(description = "用户信息实体类")
@TableName(value ="sys_user")
@Data
public class SysUser {

    @Schema(description = "用户唯一标识，自增主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "系统登录用户名，唯一且仅支持字母/数字组合")
    private String username;

    @Schema(description = "用户核心密码的PBKDF2WithHmacSHA256哈希值（不可逆）")
    private String userPasswordHash;

    @Schema(description = "用户邮箱，用于密码修改")
    private String email;

    @Schema(description = "核心密码哈希专用的64位随机盐值，Base64编码存储")
    private String salt;

    @Schema(description = "用户专属AES-256密钥（经RSA-4096公钥加密后Base64编码存储）")
    private String aesKeyRsaEncrypted;

    @Schema(description = "AES密钥版本号（用于密钥轮换）")
    private Integer aesKeyVersion;

    @Schema(description = "账号创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "账号信息更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}