CREATE DATABASE IF NOT EXISTS order_db_02 DEFAULT CHARACTER SET utf8mb4;

use order_db_02;
CREATE TABLE IF NOT EXISTS t_order (
                                       id BIGINT NOT NULL PRIMARY KEY,
                                       order_no VARCHAR(50),
    user_id BIGINT,
    amount DECIMAL(10,2),
    status VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE order_db_02;
CREATE TABLE IF NOT EXISTS t_dict (
                                      id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                                      dict_code VARCHAR(100) NOT NULL,
    dict_name VARCHAR(100),
    dict_value VARCHAR(500),
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


USE order_db_02;
INSERT INTO t_order (id, order_no, user_id, amount, status, create_time, update_time)
VALUES (
           2025561711095955457,           -- 分布式 ID
           'TEST_ORDER_001',              -- 订单号
           1001,                          -- 用户 ID
           199.00,                        -- 金额
           'CREATED',                     -- 状态
           NOW(),                         -- 创建时间
           NOW()                          -- 更新时间
       );
INSERT INTO t_dict (dict_code, dict_name, dict_value, remark)
VALUES (
           'ORDER_STATUS',                -- 字典编码
           '订单状态',                     -- 字典名称
           'CREATED,PAID,SHIPPED,CANCELLED', -- 字典值
           '订单状态字典'                  -- 备注
       );

DROP USER IF EXISTS 'repl'@'%';
CREATE USER 'repl'@'%' IDENTIFIED BY 'repl_password';
GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
FLUSH PRIVILEGES;


SELECT user, host FROM mysql.user WHERE user = 'repl';

SHOW MASTER STATUS;

# LAPTOP-KFEHPOHJ-bin.000129

SHOW VARIABLES LIKE 'server_id';

SHOW VARIABLES LIKE 'log_bin';
SHOW VARIABLES LIKE 'binlog_format';

USE mysql;
SELECT user, host, plugin FROM user WHERE user = 'repl';

ALTER USER 'repl'@'%' IDENTIFIED WITH mysql_native_password BY 'repl_password';
FLUSH PRIVILEGES;

SELECT user, host, plugin FROM user WHERE user = 'repl';

