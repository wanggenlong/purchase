# 数据库设计文档

```sql
CREATE DATABASE IF NOT EXISTS purchase_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE purchase_db;

DROP TABLE IF EXISTS t_category;
CREATE TABLE IF NOT EXISTS t_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    category_no VARCHAR(5) NOT NULL COMMENT '分类编码，1位=一级，3位=二级，4位=三级，5位=四级',
    category_name VARCHAR(64) NOT NULL COMMENT '分类名称',
    parent_no VARCHAR(5) NOT NULL DEFAULT '' COMMENT '父分类编码，空字符串表示顶级',
    level TINYINT NOT NULL DEFAULT 1 COMMENT '分类层级 1-4',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_no (category_no),
    KEY idx_parent_no (parent_no),
    KEY idx_category_name (category_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ===========================================
-- 商品分类初始数据（4级类目）
-- ===========================================

-- 一级类目
INSERT INTO t_category (category_no, category_name, parent_no, level, sort_order) VALUES
('1', '电子产品', '', 1, 1),
('2', '办公用品', '', 1, 2),
('3', '家居生活', '', 1, 3),
('4', '食品饮料', '', 1, 4);

-- 二级类目
INSERT INTO t_category (category_no, category_name, parent_no, level, sort_order) VALUES
('101', '通讯设备', '1', 2, 1),
('102', '计算机设备', '1', 2, 2),
('103', '办公设备', '2', 2, 1),
('104', '文具用品', '2', 2, 2),
('105', '纸制品', '2', 2, 3),
('106', '清洁用品', '3', 2, 1),
('107', '家纺用品', '3', 2, 2),
('108', '休闲零食', '4', 2, 1),
('109', '饮品', '4', 2, 2);

-- 三级类目
INSERT INTO t_category (category_no, category_name, parent_no, level, sort_order) VALUES
('1011', '智能手机', '101', 3, 1),
('1012', '功能机', '101', 3, 2),
('1021', '笔记本电脑', '102', 3, 1),
('1022', '台式机', '102', 3, 2),
('1031', '打印机', '103', 3, 1),
('1032', '扫描仪', '103', 3, 2),
('1041', '书写工具', '104', 3, 1),
('1042', '文件管理', '104', 3, 2),
('1051', '复印纸', '105', 3, 1),
('1052', '打印纸', '105', 3, 2),
('1061', '清洁剂', '106', 3, 1),
('1062', '清洁工具', '106', 3, 2),
('1071', '毛巾浴巾', '107', 3, 1),
('1072', '床品套件', '107', 3, 2),
('1081', '坚果炒货', '108', 3, 1),
('1082', '饼干糕点', '108', 3, 2),
('1091', '茶饮', '109', 3, 1),
('1092', '咖啡', '109', 3, 2);

-- 四级类目
INSERT INTO t_category (category_no, category_name, parent_no, level, sort_order) VALUES
('10111', '5G手机', '1011', 4, 1),
('10112', '4G手机', '1011', 4, 2),
('10121', '老人机', '1012', 4, 1),
('10211', '游戏本', '1021', 4, 1),
('10212', '商务本', '1021', 4, 2),
('10213', '轻薄本', '1021', 4, 3),
('10221', '品牌整机', '1022', 4, 1),
('10222', '组装机', '1022', 4, 2),
('10311', '激光打印机', '1031', 4, 1),
('10312', '喷墨打印机', '1031', 4, 2),
('10321', '平板扫描仪', '1032', 4, 1),
('10411', '中性笔', '1041', 4, 1),
('10412', '钢笔', '1041', 4, 2),
('10421', '文件夹', '1042', 4, 1),
('10422', '档案盒', '1042', 4, 2),
('10511', 'A4复印纸', '1051', 4, 1),
('10512', 'A3复印纸', '1051', 4, 2),
('10521', 'A4打印纸', '1052', 4, 1),
('10611', '多功能清洁剂', '1061', 4, 1),
('10612', '玻璃清洁剂', '1061', 4, 2),
('10621', '拖把', '1062', 4, 1),
('10622', '扫帚', '1062', 4, 2),
('10711', '面巾', '1071', 4, 1),
('10712', '浴巾', '1071', 4, 2),
('10721', '四件套', '1072', 4, 1),
('10722', '被芯', '1072', 4, 2),
('10811', '混合坚果', '1081', 4, 1),
('10812', '夏威夷果', '1081', 4, 2),
('10821', '苏打饼干', '1082', 4, 1),
('10822', '曲奇饼干', '1082', 4, 2),
('10911', '绿茶', '1091', 4, 1),
('10912', '红茶', '1091', 4, 2),
('10921', '速溶咖啡', '1092', 4, 1),
('10922', '咖啡豆', '1092', 4, 2);

CREATE TABLE IF NOT EXISTS t_product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    sku_code VARCHAR(64) NOT NULL COMMENT 'SKU编码',
    category_no VARCHAR(5) DEFAULT NULL COMMENT '分类编码',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '采购价',
    description VARCHAR(512) DEFAULT NULL COMMENT '商品描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sku_code (sku_code),
    KEY idx_category_no (category_no)
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
