-- =============================================================
-- 数据库改名：pme_system -> cipherlens
-- MySQL 不支持 RENAME DATABASE，用 RENAME TABLE 逐表迁移（秒级、不复制数据）
-- 执行前请先备份：
--   mysqldump -uroot -p --databases pme_system --routines --triggers > pme_system.bak.sql
-- 执行：
--   mysql -uroot -p < backend/sql/rename-database.sql
-- =============================================================

SET @db_old = 'pme_system';
SET @db_new = 'cipherlens';

CREATE DATABASE IF NOT EXISTS `cipherlens`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

SET SESSION group_concat_max_len = 1024 * 1024;

SELECT GROUP_CONCAT(
         CONCAT('`', @db_old, '`.`', table_name, '` TO `', @db_new, '`.`', table_name, '`')
         SEPARATOR ', '
       )
  INTO @tables
  FROM information_schema.tables
 WHERE table_schema = @db_old
   AND table_type = 'BASE TABLE';

SET @sql = IF(@tables IS NULL,
              'SELECT ''pme_system 中无表，跳过'' AS result',
              CONCAT('RENAME TABLE ', @tables));

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 确认 cipherlens 表数量正确后再删除空库（这一步不可回滚，注释掉可保留旧库名）
-- DROP DATABASE `pme_system`;

-- 注意：视图/存储过程/触发器不会随 RENAME TABLE 迁移。
-- 如果原库有这些对象，请改用 mysqldump 导出后导入 cipherlens。
