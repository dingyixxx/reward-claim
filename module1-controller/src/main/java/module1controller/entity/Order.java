package module1controller.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    @TableField("product_name")
    public String productName;

    private BigDecimal price;

    @TableField("quantity")
    public Integer quantity;

    private BigDecimal totalAmount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已完成，3-已取消
     */
    private Integer status;

    // 添加 JsonFormat 注解解决序列化问题
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
//
//CREATE TABLE `order` (
//        `id` bigint(20) NOT NULL primary key AUTO_INCREMENT,
//    `order_no` varchar(64) NOT NULL DEFAULT 'aa',
//        `user_id` bigint(20) NOT NULL DEFAULT '0',
//        `product_name` varchar(64) NOT NULL,
//    `price` decimal(10,2) NOT NULL DEFAULT '0',
//        `quantity` int(11) NOT NULL,
//    `total_amount` decimal(10,2) NOT NULL DEFAULT '0',
//        `status` tinyint(4) NOT NULL DEFAULT '0',
//        `create_time` datetime NOT NULL,
//        `update_time` datetime NOT NULL
//)
//

//DELIMITER $$
//drop procedure if exists InsertOrderWithSleep;
//
//
//CREATE PROCEDURE InsertOrderWithSleep(
//        IN productName VARCHAR(255),
//IN quantity INT,
//IN orderNo VARCHAR(255)
//)
//BEGIN
//DECLARE insertedId BIGINT;
//
//    -- 插入订单记录
//INSERT INTO `order` (product_name, quantity, order_no)
//VALUES (productName, quantity, orderNo);
//
//
//    -- 查询并返回刚插入的订单信息
//SELECT * ,SLEEP(FLOOR(2 + RAND() * 4)) s FROM `order` WHERE order_no = orderNo limit 1;
//END$$
//
//        DELIMITER ;


