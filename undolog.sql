CREATE TABLE `undo_log` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `branch_id` bigint NOT NULL COMMENT '分支事务ID',
                            `xid` varchar(100) NOT NULL COMMENT '全局事务ID',
                            `context` varchar(128) NOT NULL COMMENT '上下文信息',
                            `rollback_info` longblob NOT NULL COMMENT '回滚数据',
                            `log_status` int NOT NULL COMMENT '日志状态 0:正常 1:已清理',
                            `log_created` datetime NOT NULL COMMENT '创建时间',
                            `log_modified` datetime NOT NULL COMMENT '修改时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AT 事务模式回滚日志表'