-- ============================================
-- 青知共享平台 - 完整数据库设计
-- ============================================

CREATE DATABASE IF NOT EXISTS qingzhi_db DEFAULT CHARACTER SET utf8mb4;
USE qingzhi_db;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '学号/工号',
    password VARCHAR(255) NOT NULL COMMENT 'bcrypt加密',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    department VARCHAR(100) DEFAULT NULL COMMENT '院系',
    major VARCHAR(100) DEFAULT NULL COMMENT '专业',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '0学生 1教师 2管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间，NULL表示有效',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 登录日志表（防暴力破解）
CREATE TABLE IF NOT EXISTS login_logs (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT '登录IP',
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    success TINYINT NOT NULL DEFAULT 0 COMMENT '0失败 1成功',
    PRIMARY KEY (id),
    KEY idx_user_time (user_id, login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 3. 文件表（支持MD5秒传去重）
CREATE TABLE IF NOT EXISTS files (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    storage_name VARCHAR(255) NOT NULL COMMENT 'UUID存储名',
    file_path VARCHAR(500) NOT NULL COMMENT '存储路径',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    file_type VARCHAR(150) NOT NULL COMMENT 'MIME类型',
    md5_hash VARCHAR(32) NOT NULL COMMENT 'MD5哈希值',
    upload_user_id BIGINT NOT NULL COMMENT '上传者ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_md5_hash (md5_hash),
    KEY idx_upload_user_id (upload_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- 4. 资源表
CREATE TABLE IF NOT EXISTS resources (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '资源ID',
    title VARCHAR(200) NOT NULL COMMENT '资源标题',
    description TEXT COMMENT '资源描述',
    course VARCHAR(100) DEFAULT NULL COMMENT '所属课程',
    file_id BIGINT NOT NULL COMMENT '关联文件ID',
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0待审核 1已通过 2已拒绝',
    reject_reason VARCHAR(500) DEFAULT NULL COMMENT '拒绝理由',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间，NULL表示有效',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_user_id (user_id),
    KEY idx_file_id (file_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源表';

-- 5. 收藏表
CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间，NULL表示有效',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_resource (user_id, resource_id),
    KEY idx_resource_id (resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 初始化管理员账号 (配置类自动注册)
