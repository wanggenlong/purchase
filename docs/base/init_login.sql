-- ===========================================
-- 采购管理系统 - 登录模块SQL初始化脚本
-- 执行方式: 在MySQL客户端中执行
-- ===========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS purchase_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE purchase_db;

-- ===========================================
-- 用户表（登录模块）
-- ===========================================
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名（登录账号）',
    password VARCHAR(128) NOT NULL COMMENT '密码（BCrypt加密存储）',
    real_name VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    phone VARCHAR(32) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态 1-启用 0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ===========================================
-- 初始化测试用户数据
-- 用户名: admin
-- 密码: 123456
-- ===========================================
-- 先删除可能存在的旧数据
DELETE FROM t_user WHERE username = 'admin';

-- 插入正确的用户，密码是BCrypt加密后的123456
-- 这个哈希是用Spring Security BCryptPasswordEncoder生成的
INSERT INTO t_user (username, password, real_name, phone, email, status) VALUES 
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU9uDmVm0S2', '系统管理员', '13800138000', 'admin@purchase.com', 1);

-- 验证数据
SELECT id, username, real_name, status, password FROM t_user;