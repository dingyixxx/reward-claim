CREATE DATABASE IF NOT EXISTS order_db_01 DEFAULT CHARACTER SET utf8mb4;

use order_db_01;
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

USE order_db_01;
CREATE TABLE IF NOT EXISTS t_dict (
                                      id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                                      dict_code VARCHAR(100) NOT NULL,
    dict_name VARCHAR(100),
    dict_value VARCHAR(500),
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 从 SHOW MASTER STATUS 获取

CHANGE MASTER TO
    MASTER_HOST='192.168.0.111',
    MASTER_PORT=3306,
    MASTER_USER='repl',
    MASTER_PASSWORD='repl_password',
    MASTER_LOG_FILE='LAPTOP-KFEHPOHJ-bin.000129',
    MASTER_LOG_POS=36596745,
    MASTER_SSL=1;


START SLAVE;

SHOW VARIABLES LIKE 'server_id';

SHOW SLAVE STATUS;

STOP SLAVE;

RESET SLAVE ALL;

USE mysql;

SHOW TABLES LIKE 'slave_%';

SHOW CREATE TABLE slave_master_info;

REPAIR TABLE slave_master_info;
REPAIR TABLE slave_relay_log_info;
REPAIR TABLE slave_worker_info;

