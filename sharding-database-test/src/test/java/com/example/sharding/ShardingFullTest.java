package com.example.sharding;

import com.example.sharding.entity.Dict;
import com.example.sharding.entity.Order;
import com.example.sharding.mapper.DictMapper;
import com.example.sharding.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@SpringBootTest
public class ShardingFullTest {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DictMapper dictMapper;

    @Test
    public void testFull() throws InterruptedException {
        log.info("=========================================");
        log.info("  综合测试：分库 + 读写分离 + 广播表");
        log.info("=========================================");

//        // 1. 广播表测试
//        log.info("【1】测试广播表...");
//        Dict dict = new Dict();
//        dict.setDictCode("FULL_TEST");
//        dict.setDictName("综合测试");
//        dict.setDictValue("TEST_VALUE");
//        dictMapper.insert(dict);
//        log.info("✅ 广播表插入成功");
//
//        List<Dict> dicts = dictMapper.selectList(null);
//        log.info("✅ 广播表查询成功，共 {} 条", dicts.size());
//
//        // 2. 分库 + 读写分离测试
//        log.info("【2】测试分库 + 读写分离...");

//        // 偶数 user_id → ds_primary
//        Order order1 = new Order();
//        order1.setId(2025561711095955459L);
//        order1.setOrderNo("FULL_TEST_EVEN");
//        order1.setUserId(1000L);  // 偶数
//        order1.setAmount(new BigDecimal("100.00"));
//        order1.setStatus("CREATED");
//        order1.setCreateTime(LocalDateTime.now());
//        order1.setUpdateTime(LocalDateTime.now());
//        orderMapper.insert(order1);
//        log.info("✅ 偶数 user_id 写入成功 (ds_primary)");
//
//        // 奇数 user_id → ds_replica
//        Order order2 = new Order();
//        order2.setId(2025561711095955460L);
//        order2.setOrderNo("FULL_TEST_ODD");
//        order2.setUserId(1001L);  // 奇数
//        order2.setAmount(new BigDecimal("200.00"));
//        order2.setStatus("PAID");
//        order2.setCreateTime(LocalDateTime.now());
//        order2.setUpdateTime(LocalDateTime.now());
//        orderMapper.insert(order2);
//        log.info("✅ 奇数 user_id 写入成功 (ds_replica)");
//
//        // 等待主从同步
//        Thread.sleep(500);

//        // 读操作（应该走从库）
//        Order readOrder = orderMapper.selectById(order1.getId());
//        log.info("✅ 读操作成功 (ds_replica)");
//
//        // 强制读主库
//        try (HintManager hintManager = HintManager.getInstance()) {
//            hintManager.setDatabaseShardingValue("ds_primary"); // ✅ 正确：单参数，指定主库数据源名
//            Order masterOrder = orderMapper.selectById(order2.getId());
//            log.info("✅ 强制读主库成功 (ds_primary)");
//        }
//
//        // 3. 验证数据
//        log.info("【3】验证数据...");
        List<Order> allOrders = orderMapper.selectList(null);
        log.info("✅ 总订单数：{}", allOrders.size());
        for (Order o : allOrders) {
            log.info("订单：{}", o);
        }
//
//        log.info("=========================================");
//        log.info("  综合测试完成！");
//        log.info("=========================================");
    }
}
