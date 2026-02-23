package com.example.sharding.service;

import com.example.sharding.entity.Order;
import com.example.sharding.mapper.OrderMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@NoArgsConstructor
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 创建订单（写操作 → 主库）
     */
    public Order createOrder(Long userId, BigDecimal amount, String orderNo) {
        Order order = new Order();
        order.setId(System.currentTimeMillis());  // 或用雪花算法
        order.setUserId(userId);
        order.setAmount(amount);
        order.setOrderNo(orderNo != null ? orderNo : "ORD_" + System.currentTimeMillis());
        order.setStatus("CREATED");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);
        log.info("【写】订单写入主库：id={}, userId={}", order.getId(), userId);
        return order;
    }

    /**
     * 查询订单（读操作 → 从库）
     */
    public Order getOrder(Long id) {
        Order order = orderMapper.selectById(id);
        log.info("【读】订单从从库读取：id={}", id);
        return order;
    }

    /**
     * 强制读主库
     */
    public Order getOrderFromMaster(Long id) {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setDatabaseShardingValue("ds0");
            Order order = orderMapper.selectById(id);
            log.info("【强制读主】订单从主库读取：id={}", id);
            return order;
        }
    }

    /**
     * 根据用户 ID 查询
     */
    public List<Order> getByUserId(Long userId) {
        return orderMapper.selectByUserId(userId);
    }

    /**
     * 查询所有订单
     */
    public List<Order> getAllOrders() {
        return orderMapper.selectList(null);
    }
}
