package com.example.sharding.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 对应分片表：t_order_YYYYMMDDHH
 */
@Data
@TableName("t_order")
public class Order {

    /**
     * 订单 ID（分布式 ID，雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 创建时间（分片键）
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
//
//CREATE TABLE `t_order_2026022218` (
//        `id` bigint NOT NULL,
//        `order_no` varchar(50) DEFAULT NULL,
//                                      `user_id` bigint DEFAULT NULL,
//        `amount` decimal(10,2) DEFAULT NULL,
//                                      `create_time` datetime NOT NULL DEFAULT (now()),
//        `update_time` datetime NOT NULL DEFAULT (now()),
//PRIMARY KEY (`id`)
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci