package com.example.sharding;

import com.example.sharding.entity.Order;
import com.example.sharding.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@SpringBootTest
public class ReadWriteSplittingTest {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 测试读写分离
     */
    @Test
    public void testReadWriteSplitting() throws InterruptedException {
        log.info("========== 测试：读写分离 ==========");

        // 1. 写入操作（应该路由到主库 3306）
        log.info("1. 写入订单（主库 3306）...");
        Order order = new Order();
        order.setId(2025561711095955457L);
//        order.setOrderNo("RW_TEST_" + System.currentTimeMillis());
//        order.setUserId(1002L);  // 奇数 → ds_replica
//        order.setAmount(new BigDecimal("199.00"));
//        order.setStatus("CREATED");
//        order.setCreateTime(LocalDateTime.now());
//        order.setUpdateTime(LocalDateTime.now());
//        orderMapper.insert(order);
//        log.info("✅ 写入成功，订单 ID: {}", order.getId());
//
//        // 2. 等待主从同步
//        Thread.sleep(500);

        // 3. 读取操作（应该路由到从库 3307）
        log.info("2. 读取订单（从库 3307）...");
        Order readOrder = orderMapper.selectById(order.getId());
        log.info("✅ 从库读取成功：orderNo={}", readOrder.getOrderNo());

        // 4. 强制读主库
        log.info("3. 强制读主库（3306）...");
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setWriteRouteOnly(); // ✅ 正确：单参数，指定主库数据源名
            Order masterOrder = orderMapper.selectById(order.getId());
            log.info("✅ 主库读取成功：orderNo={}", masterOrder.getOrderNo());
        }


        // 4. 强制读主库
        log.info("3. 自动读取读库（3307）...");
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setReadwriteSplittingAuto(); // ✅ 正确：单参数，指定主库数据源名
            Order masterOrder = orderMapper.selectById(order.getId());
            log.info("✅ 读库读取成功：orderNo={}", masterOrder.getOrderNo());
        }

        log.info("========== 测试完成 ==========");
        log.info("⚠️  请观察控制台日志中的 Actual SQL：");
        log.info("   - INSERT 应该显示 ds_primary");
        log.info("   - SELECT 应该显示 ds_replica");
        log.info("   - HintManager 强制读应该显示 ds_primary");
    }
}