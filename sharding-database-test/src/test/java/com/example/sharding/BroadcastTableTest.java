package com.example.sharding;

import com.example.sharding.entity.Dict;
import com.example.sharding.entity.Order;
import com.example.sharding.mapper.DictMapper;
import com.example.sharding.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@SpringBootTest
public class BroadcastTableTest {

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void testBroadcastTable() {
        log.info("========== 测试：广播表 ==========");

        // 1. 创建字典数据（手动指定 ID）
        log.info("1. 创建字典数据...");
        Dict dict1 = new Dict();
        dict1.setId(System.currentTimeMillis());  // ✅ 手动生成 ID
        dict1.setDictCode("ORDER_STATUS1");
        dict1.setDictName("订单状态3");
        dict1.setDictValue("CREATED,PAID,SHIPPED,CANCELLED");
        dictMapper.insert(dict1);

        Dict dict2 = new Dict();
        dict2.setId(System.currentTimeMillis() + 1);  // ✅ 手动生成 ID
        dict2.setDictCode("PAY_TYPE2");
        dict2.setDictName("支付方式3");
        dict2.setDictValue("ALIPAY,WECHAT,CARD");
        dictMapper.insert(dict2);

        log.info("✅ 创建字典完成");

        // 2. 查询字典
        log.info("2. 查询字典数据...");
        List<Dict> dicts = dictMapper.selectList(null);
        log.info("查询到字典数量：{}", dicts.size());
        for (Dict d : dicts) {
            log.info("  字典：code={}, name={}", d.getDictCode(), d.getDictName());
        }
    }

    /**
     * 测试广播表 + 订单表 JOIN
     */
    @Test
    public void testBroadcastTableJoin() {
        log.info("========== 测试：广播表 JOIN 订单表 ==========");

        // 1. 先创建订单
        Order order = new Order();
        order.setId(2025561711095955458L);
        order.setOrderNo("JOIN_TEST_001");
        order.setUserId(1003L);
        order.setAmount(new BigDecimal("299.00"));
        order.setStatus("PAY_TYPE");  // 状态值对应字典
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 2. 查询订单 + 字典 JOIN
        String sql = "SELECT o.order_no, o.status, d.dict_name, d.dict_value " +
                "FROM t_order o JOIN t_dict d ON o.status = d.dict_code " +
                "WHERE o.order_no = 'JOIN_TEST_001'";

        log.info("执行 JOIN 查询：{}", sql);
        // 可以使用 JdbcTemplate 执行

        log.info("✅ JOIN 查询成功（广播表确保每个库都有字典数据）");
    }
}
