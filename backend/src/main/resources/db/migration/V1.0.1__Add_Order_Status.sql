CREATE TABLE order_status (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    sequence INT,
    is_active BOOLEAN DEFAULT TRUE,
    create_time DATETIME,
    update_time DATETIME
);

INSERT INTO order_status (code, name, description, sequence, create_time, update_time) VALUES
(0, '待确认', '订单已创建，等待确认', 1, NOW(), NOW()),
(1, '已确认', '订单已确认，等待生产', 2, NOW(), NOW()),
(2, '生产中', '订单正在生产中', 3, NOW(), NOW()),
(3, '已发货', '订单已发货，等待送达', 4, NOW(), NOW()),
(4, '已完成', '订单已完成', 5, NOW(), NOW()),
(5, '已取消', '订单已取消', 6, NOW(), NOW()); 