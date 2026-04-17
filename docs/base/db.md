# 数据库设计文档

```sql
CREATE DATABASE IF NOT EXISTS purchase_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE purchase_db;

CREATE TABLE IF NOT EXISTS t_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    category_name VARCHAR(64) NOT NULL COMMENT '分类名称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_category_name (category_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

CREATE TABLE IF NOT EXISTS t_product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    sku_code VARCHAR(64) NOT NULL COMMENT 'SKU编码',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '采购价',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sku_code (sku_code),
    KEY idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE IF NOT EXISTS t_purchase_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(64) NOT NULL COMMENT '采购单号',
    supplier_name VARCHAR(128) NOT NULL COMMENT '供应商名称',
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '采购总金额',
    order_status VARCHAR(32) NOT NULL COMMENT '采购单状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYCOUNT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_supplier_name (supplier_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单表';

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

-- 初始化测试用户数据（密码: 123456）
INSERT INTO t_user (username, password, real_name, phone, email, status) VALUES
('admin', '$2a$10$M81rJ.LMwhGzM3VXFEM9d.14O3EgTCqw9j50TRba/UbqT0qxGyuRu', '系统管理员', '13800138000', 'admin@purchase.com', 1);
```
