-- =============================================================
-- 密鉴 CipherLens 数据库初始化脚本
-- 由 Docker 首次启动 MySQL 时自动执行（mysql-data 卷为空时）
-- 手工执行：mysql -uroot -p < backend/sql/init.sql
-- =============================================================

CREATE DATABASE IF NOT EXISTS `cipherlens`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `cipherlens`;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user`
(
    `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`              VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    `user_password_hash`    VARCHAR(255) NOT NULL COMMENT '核心密码 PBKDF2 哈希',
    `email`                 VARCHAR(128)          DEFAULT NULL COMMENT '邮箱',
    `salt`                  VARCHAR(128) NOT NULL COMMENT 'PBKDF2 盐值(Base64)',
    `aes_key_rsa_encrypted` TEXT         NOT NULL COMMENT 'RSA 加密后的 AES-256 密钥',
    `aes_key_version`       INT          NOT NULL DEFAULT 1 COMMENT 'AES 密钥版本',
    `create_time`           DATETIME     NOT NULL COMMENT '创建时间',
    `update_time`           DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- 密码条目表
CREATE TABLE IF NOT EXISTS `password_item`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '密码条目ID',
    `user_id`     BIGINT       NOT NULL COMMENT '所属用户ID',
    `app_name`    VARCHAR(128) NOT NULL COMMENT '应用/网站名称',
    `account`     VARCHAR(128) NOT NULL COMMENT '登录账号',
    `password`    TEXT         NOT NULL COMMENT '密码(AES-256-GCM 密文)',
    `remark`      VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='密码条目表';

-- 密码安全评估记录表
CREATE TABLE IF NOT EXISTS `password_evaluation_log`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评估记录ID',
    `user_id`          BIGINT       NOT NULL COMMENT '用户ID',
    `password_item_id` BIGINT       NOT NULL COMMENT '密码条目ID',
    `evaluate_score`   INT          NOT NULL COMMENT '评估分数',
    `risk_type`        VARCHAR(64)           DEFAULT NULL COMMENT '风险类型',
    `suggestions`      TEXT                  DEFAULT NULL COMMENT '改进建议',
    `evaluate_time`    DATETIME     NOT NULL COMMENT '评估时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_item_id` (`password_item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='密码安全评估记录表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log`
(
    `id`                BIGINT      NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id`           BIGINT               DEFAULT NULL COMMENT '用户ID',
    `operation_type`    VARCHAR(64)          DEFAULT NULL COMMENT '操作类型',
    `operation_content` VARCHAR(512)         DEFAULT NULL COMMENT '操作内容',
    `operation_result`  INT                  DEFAULT NULL COMMENT '操作结果',
    `cost_time`         BIGINT               DEFAULT NULL COMMENT '耗时(ms)',
    `operation_time`    DATETIME    NOT NULL COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `operation_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='操作日志表';

-- 弱密码字典表
CREATE TABLE IF NOT EXISTS `weak_password`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `password`    VARCHAR(255) NOT NULL COMMENT '弱密码',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_password` (`password`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='弱密码字典表';

-- 弱密码种子数据（极简版，生产环境建议导入完整字典）
INSERT IGNORE INTO `weak_password` (`password`, `create_time`)
VALUES ('123456', NOW()),
       ('11111111', NOW()),
       ('123456789', NOW()),
       ('1234567890', NOW()),
       ('111111', NOW()),
       ('000000', NOW()),
       ('666666', NOW()),
       ('888888', NOW()),
       ('123123', NOW()),
       ('abc123', NOW()),
       ('a123456', NOW()),
       ('qwerty', NOW()),
       ('qwerty123', NOW()),
       ('password', NOW()),
       ('password1', NOW()),
       ('passw0rd', NOW()),
       ('admin', NOW()),
       ('admin123', NOW()),
       ('root', NOW()),
       ('letmein', NOW()),
       ('welcome', NOW()),
       ('iloveyou', NOW()),
       ('monkey', NOW()),
       ('dragon', NOW()),
       ('sunshine', NOW()),
       ('princess', NOW()),
       ('football', NOW()),
       ('baseball', NOW()),
       ('superman', NOW()),
       ('1qaz2wsx', NOW()),
       ('qazwsx', NOW()),
       ('zxcvbnm', NOW()),
       ('asdfgh', NOW()),
       ('1q2w3e4r', NOW()),
       ('woaini', NOW()),
       ('woaini1314', NOW()),
       ('5201314', NOW()),
       ('a1b2c3', NOW()),
       ('changeme', NOW()),
       ('test123', NOW());
