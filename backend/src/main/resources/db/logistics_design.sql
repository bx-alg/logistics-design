-- 创建数据库
CREATE DATABASE IF NOT EXISTS logistics_design DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE logistics_design;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `product_code` varchar(50) NOT NULL COMMENT '商品编码',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `description` varchar(500) DEFAULT NULL COMMENT '商品描述',
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片URL',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_code` (`product_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `product_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) DEFAULT 0 COMMENT '父级ID',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `total_amount` decimal(12,2) NOT NULL COMMENT '订单总额',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待确认，1-已确认，2-生产中，3-已发货，4-已完成，5-已取消',
  `address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `expected_delivery_date` date DEFAULT NULL COMMENT '预计交付日期',
  `actual_delivery_date` date DEFAULT NULL COMMENT '实际交付日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '单价',
  `total_price` decimal(12,2) NOT NULL COMMENT '总价',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 生产计划表
CREATE TABLE IF NOT EXISTS `production_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `plan_no` varchar(50) NOT NULL COMMENT '计划编号',
  `order_id` bigint(20) NOT NULL COMMENT '关联订单ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `plan_quantity` int(11) NOT NULL COMMENT '计划生产数量',
  `actual_quantity` int(11) DEFAULT 0 COMMENT '实际生产数量',
  `start_date` date NOT NULL COMMENT '计划开始日期',
  `end_date` date NOT NULL COMMENT '计划结束日期',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '状态：0-待生产，1-生产中，2-已完成，3-已取消',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_no` (`plan_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产计划表';

-- 库存表
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `quantity` int(11) NOT NULL DEFAULT 0 COMMENT '库存数量',
  `reserved_quantity` int(11) NOT NULL DEFAULT 0 COMMENT '预留数量',
  `available_quantity` int(11) GENERATED ALWAYS AS (`quantity` - `reserved_quantity`) STORED COMMENT '可用数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_warehouse` (`product_id`,`warehouse_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 仓库表
CREATE TABLE IF NOT EXISTS `warehouse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
  `warehouse_name` varchar(100) NOT NULL COMMENT '仓库名称',
  `warehouse_code` varchar(50) NOT NULL COMMENT '仓库编码',
  `address` varchar(255) DEFAULT NULL COMMENT '仓库地址',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_warehouse_code` (`warehouse_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 运输单表
CREATE TABLE IF NOT EXISTS `shipment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '运输单ID',
  `shipment_no` varchar(50) NOT NULL COMMENT '运输单号',
  `order_id` bigint(20) NOT NULL COMMENT '关联订单ID',
  `carrier_id` bigint(20) NOT NULL COMMENT '承运商ID',
  `from_warehouse_id` bigint(20) NOT NULL COMMENT '发货仓库ID',
  `to_address` varchar(255) NOT NULL COMMENT '收货地址',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `estimated_departure_time` datetime DEFAULT NULL COMMENT '预计出发时间',
  `estimated_arrival_time` datetime DEFAULT NULL COMMENT '预计到达时间',
  `actual_departure_time` datetime DEFAULT NULL COMMENT '实际出发时间',
  `actual_arrival_time` datetime DEFAULT NULL COMMENT '实际到达时间',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '状态：0-待发货，1-已发货，2-运输中，3-已送达，4-已取消',
  `tracking_number` varchar(100) DEFAULT NULL COMMENT '物流追踪号',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shipment_no` (`shipment_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运输单表';

-- 运输物品表
CREATE TABLE IF NOT EXISTS `shipment_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shipment_id` bigint(20) NOT NULL COMMENT '运输单ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_shipment_id` (`shipment_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运输物品表';

-- 承运商表
CREATE TABLE IF NOT EXISTS `carrier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '承运商ID',
  `carrier_name` varchar(100) NOT NULL COMMENT '承运商名称',
  `carrier_code` varchar(50) NOT NULL COMMENT '承运商编码',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_carrier_code` (`carrier_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='承运商表';

-- 供应商表
CREATE TABLE IF NOT EXISTS `supplier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `supplier_name` varchar(100) NOT NULL COMMENT '供应商名称',
  `supplier_code` varchar(50) NOT NULL COMMENT '供应商编码',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `rating` char(1) DEFAULT 'C' COMMENT '评级：A/B/C/D/E',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_supplier_code` (`supplier_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 供应商绩效评估表
CREATE TABLE IF NOT EXISTS `supplier_performance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `evaluation_date` date NOT NULL COMMENT '评估日期',
  `delivery_score` decimal(5,2) DEFAULT 0 COMMENT '交付评分',
  `quality_score` decimal(5,2) DEFAULT 0 COMMENT '质量评分',
  `price_score` decimal(5,2) DEFAULT 0 COMMENT '价格评分',
  `service_score` decimal(5,2) DEFAULT 0 COMMENT '服务评分',
  `total_score` decimal(5,2) DEFAULT 0 COMMENT '总评分',
  `rating` char(1) DEFAULT 'C' COMMENT '评级：A/B/C/D/E',
  `evaluator_id` bigint(20) NOT NULL COMMENT '评估人ID',
  `comments` varchar(500) DEFAULT NULL COMMENT '评价意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_supplier_date` (`supplier_id`,`evaluation_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商绩效评估表';

-- 客户表
CREATE TABLE IF NOT EXISTS `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  `customer_name` varchar(100) NOT NULL COMMENT '客户名称',
  `customer_code` varchar(50) NOT NULL COMMENT '客户编码',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_code` (`customer_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 插入初始角色数据
INSERT INTO `sys_role` (role_name, role_code, description) VALUES 
('管理员', 'ADMIN', '系统管理员'),
('企业用户', 'ENTERPRISE', '企业用户'),
('物流用户', 'LOGISTICS', '物流服务商'),
('供应商用户', 'SUPPLIER', '供应商');

-- 插入初始管理员用户（密码：123456）
INSERT INTO `sys_user` (username, password, real_name, role_id) VALUES 
('admin', '$2a$10$VQgBnBYVQRJ9aeA9Jfh.9uIxiG9qRGRZiZ7KV.lGKK9vSz8MHkYX.', '系统管理员', 1); 