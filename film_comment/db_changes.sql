-- Add VIP field to user table
ALTER TABLE `user` ADD COLUMN `is_vip` tinyint(1) DEFAULT 0 COMMENT 'VIP状态：0-普通用户，1-VIP用户';

-- Create seat table
CREATE TABLE `seat` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `row` int(11) NOT NULL COMMENT '行号',
  `column` int(11) NOT NULL COMMENT '列号',
  `hall_id` int(11) NOT NULL COMMENT '影厅ID',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-不可用，1-可用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '座位信息' ROW_FORMAT = Dynamic;

-- Create hall table
CREATE TABLE `hall` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '影厅名称',
  `row_count` int(11) NOT NULL COMMENT '行数',
  `column_count` int(11) NOT NULL COMMENT '列数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '影厅信息' ROW_FORMAT = Dynamic;

-- Create screening table
CREATE TABLE `screening` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `film_id` int(11) NOT NULL COMMENT '电影ID',
  `hall_id` int(11) NOT NULL COMMENT '影厅ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `price` decimal(10,2) NOT NULL COMMENT '票价',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '放映场次' ROW_FORMAT = Dynamic;

-- Create order table
CREATE TABLE `order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `screening_id` int(11) NOT NULL COMMENT '场次ID',
  `seat_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '座位ID，多个用逗号分隔',
  `total_price` decimal(10,2) NOT NULL COMMENT '总价',
  `discount_price` decimal(10,2) DEFAULT 0 COMMENT '折扣金额',
  `actual_price` decimal(10,2) NOT NULL COMMENT '实际支付金额',
  `status` tinyint(1) DEFAULT 0 COMMENT '状态：0-未支付，1-已支付，2-已取消',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `coupon_id` int(11) DEFAULT NULL COMMENT '优惠券ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `order_no` (`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单信息' ROW_FORMAT = Dynamic;

-- Create coupon table
CREATE TABLE `coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '优惠券名称',
  `discount` decimal(10,2) NOT NULL COMMENT '折扣率，如0.8表示8折',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `is_holiday` tinyint(1) DEFAULT 0 COMMENT '是否节日优惠券：0-否，1-是',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-无效，1-有效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '优惠券信息' ROW_FORMAT = Dynamic;

-- Create user_coupon table for mapping users and coupons
CREATE TABLE `user_coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `coupon_id` int(11) NOT NULL COMMENT '优惠券ID',
  `is_used` tinyint(1) DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
  `get_time` datetime NOT NULL COMMENT '获取时间',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户优惠券' ROW_FORMAT = Dynamic;

-- Insert sample data for halls
INSERT INTO `hall` (`name`, `row_count`, `column_count`) VALUES ('1号厅', 10, 12);
INSERT INTO `hall` (`name`, `row_count`, `column_count`) VALUES ('2号厅', 8, 10);
INSERT INTO `hall` (`name`, `row_count`, `column_count`) VALUES ('VIP厅', 6, 8);

-- Insert sample data for seats in hall 1
INSERT INTO `seat` (`row`, `column`, `hall_id`, `status`)
SELECT r.num AS `row`, c.num AS `column`, 1 AS `hall_id`, 1 AS `status`
FROM 
    (SELECT 1 AS num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 
     UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) r,
    (SELECT 1 AS num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 
     UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
     UNION SELECT 11 UNION SELECT 12) c;

-- Insert sample data for seats in hall 2
INSERT INTO `seat` (`row`, `column`, `hall_id`, `status`)
SELECT r.num AS `row`, c.num AS `column`, 2 AS `hall_id`, 1 AS `status`
FROM 
    (SELECT 1 AS num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
     UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) r,
    (SELECT 1 AS num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 
     UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) c;

-- Insert sample data for seats in hall 3 (VIP hall)
INSERT INTO `seat` (`row`, `column`, `hall_id`, `status`)
SELECT r.num AS `row`, c.num AS `column`, 3 AS `hall_id`, 1 AS `status`
FROM 
    (SELECT 1 AS num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
     UNION SELECT 5 UNION SELECT 6) r,
    (SELECT 1 AS num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
     UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) c;

-- Insert sample screenings
INSERT INTO `screening` (`film_id`, `hall_id`, `start_time`, `end_time`, `price`) 
VALUES 
(1, 1, '2024-09-10 10:00:00', '2024-09-10 12:00:00', 45.00),
(2, 2, '2024-09-10 13:00:00', '2024-09-10 15:00:00', 50.00),
(3, 3, '2024-09-10 16:00:00', '2024-09-10 18:00:00', 60.00),
(4, 1, '2024-09-10 19:00:00', '2024-09-10 21:00:00', 55.00),
(6, 2, '2024-09-11 10:00:00', '2024-09-11 13:00:00', 50.00),
(7, 3, '2024-09-11 14:00:00', '2024-09-11 16:30:00', 65.00);

-- Insert sample holiday coupons
INSERT INTO `coupon` (`name`, `discount`, `start_date`, `end_date`, `is_holiday`, `status`) 
VALUES 
('国庆节8折优惠券', 0.80, '2024-10-01', '2024-10-07', 1, 1),
('中秋节8折优惠券', 0.80, '2024-09-15', '2024-09-17', 1, 1),
('VIP专享7折优惠券', 0.70, '2024-09-01', '2024-12-31', 0, 1);
