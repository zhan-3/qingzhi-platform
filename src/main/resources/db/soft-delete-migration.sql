-- 青知共享平台：逻辑外键与软删除一次性迁移脚本
-- 注意：项目未集成 Flyway，本文件需要在现有 qingzhi_db 数据库中手动执行一次。

USE qingzhi_db;

ALTER TABLE users
    ADD COLUMN deleted_at DATETIME NULL COMMENT '软删除时间，NULL表示有效' AFTER updated_at;

ALTER TABLE resources
    ADD COLUMN deleted_at DATETIME NULL COMMENT '软删除时间，NULL表示有效' AFTER updated_at,
    ADD INDEX idx_file_id (file_id);

ALTER TABLE favorites
    ADD COLUMN deleted_at DATETIME NULL COMMENT '软删除时间，NULL表示有效' AFTER created_at,
    ADD INDEX idx_resource_id (resource_id);

ALTER TABLE files
    MODIFY COLUMN file_type VARCHAR(150) NOT NULL COMMENT 'MIME类型',
    ADD INDEX idx_upload_user_id (upload_user_id);
