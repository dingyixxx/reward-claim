package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inventory_movement")
public class InventoryMovement {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "product_id")
    private Long productId;        // 产品ID

    @TableField(value = "product_name")
    private String productName;    // 产品名称

    @TableField(value = "quantity_change")
    private Integer quantityChange; // 数量变化
    private BigDecimal amount;     // 金额

    @TableField(value = "order_id")
    private Long orderId;          // 订单ID

    @TableField(value = "order_no")
    private String orderNo;        // 订单号

    @TableField(value = "movement_type")
    private Integer movementType;  // 操作类型(IN/OUT)

    @TableField(value = "created_time")
    private LocalDateTime createTime;    // 创建时间

    // 构造函数中设置默认值
    public InventoryMovement() {
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.productId=88L;
        this.movementType = 1; // 默认出库操作
    }
}
//
//CREATE TABLE `inventory_movement` (
//        `id` bigint(20) NOT NULL primary key AUTO_INCREMENT,
//    `product_id` bigint(20) NOT NULL default 0,
//        `product_name` varchar(255) NOT NULL default '',
//        `quantity_change` int(11) NOT NULL default 0,
//        `amount` decimal(10, 2) NOT NULL default 0.00,
//        `order_id` bigint(20) NOT NULL default 0,
//        `order_no` varchar(255) NOT NULL default  '',
//        `movement_type` tinyint(4) NOT NULL,
//    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
//        `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
//
//)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
