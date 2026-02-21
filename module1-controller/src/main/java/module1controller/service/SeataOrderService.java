package module1controller.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import module1controller.entity.Order;
import module1controller.mapper.OrderMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.example.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class SeataOrderService extends ServiceImpl<OrderMapper, Order> {

    @DubboReference(version = "1.0.0", loadbalance = "random", group = "dingyi") // 支持权重
    private HelloService helloService;

    @Autowired
    private OrderService orderService;

    @GlobalTransactional(timeoutMills = 300000, name = "create-order-tx",rollbackFor = Exception.class)
    public void createOrderWithSeata(Order testOrder, Map<String, Object> operations, Map<String, Object>  result) throws Exception {
        Order createdOrder = orderService.createOrder(testOrder);
//        seata不能回滚存储过程, 它是黑盒

        operations.put("1_db_write", Map.of(
                "success", true,
                "message", "订单创建成功",
                "order_id", createdOrder.getId(),
                "order_no", createdOrder.getOrderNo(),
                "product_name", createdOrder.getProductName(),
                "price", createdOrder.getPrice(),
                "quantity", createdOrder.getQuantity()
        ));
        System.out.println("订单创建成功: " + createdOrder.getOrderNo());
//        if(1==1){
//            throw new Exception("测试异常");
//        }


        helloService.updateInventory(createdOrder.getId(), createdOrder.getOrderNo(), createdOrder.getProductName(), createdOrder.getUserId(), createdOrder.getQuantity());
        log.info("更新库存成功: 订单ID={}, 订单号={}, 商品名称={}, 用户ID={}, 数量={}", createdOrder.getId(), createdOrder.getOrderNo(), createdOrder.getProductName(), createdOrder.getUserId(), createdOrder.getQuantity());
        // 构建最终结果
        result.put("success", true);
        result.put("message", "完整测试流程执行成功");
        result.put("operations", operations);
        result.put("summary", Map.of(
                "total_operations", 7,
                "successful_operations", operations.size(),
                "test_order_id", createdOrder.getId(),
                "test_order_no", createdOrder.getOrderNo(),
                "test_product", createdOrder.getProductName(),
                "test_amount", createdOrder.getTotalAmount()
        ));

        System.out.println("=== 完整测试流程完成 ===");

    }
}
