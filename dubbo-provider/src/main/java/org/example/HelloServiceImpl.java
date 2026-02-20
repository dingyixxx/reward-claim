package org.example;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.InventoryMovement;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.example.mapper.InventoryMovementMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@DubboService(version = "1.0.0",group = "dingyi")
@Component
@Slf4j
public class HelloServiceImpl  extends ServiceImpl<InventoryMovementMapper, InventoryMovement> implements HelloService{

    public HelloServiceImpl() {
        System.out.println(">>> HelloServiceImpl constructed!");
    }


    @Override
    public String sayHello(String name) {
        System.out.println(">>> HelloServiceImpl.sayHello() invoked!");
        return "[Provider on port %d] Hello, %s!";
    }

    /**
     * 更新库存方法
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param productName 商品名称
     * @param userId 用户ID
     * @param quantity 数量
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateInventory(Long orderId, String orderNo, String productName, Long userId, Integer quantity) {
        try {
            log.info("开始更新库存: 订单ID={}, 订单号={}, 商品={}, 用户={}, 数量={}",
                    orderId, orderNo, productName, userId, quantity);

            // 创建库存变动记录
            InventoryMovement movement = new InventoryMovement();
            movement.setOrderId(orderId);
            movement.setOrderNo(orderNo);
            movement.setProductName(productName);
            movement.setQuantityChange( quantity);

            // 保存库存变动记录
            boolean saveResult = this.save(movement);

            if (saveResult) {
                log.info("库存更新成功: 订单号={}", orderNo);
            } else {
                log.error("库存更新失败: 订单号={}", orderNo);
                throw new RuntimeException("库存更新失败");
            }

        } catch (Exception e) {
            log.error("更新库存时发生异常: 订单号={}", orderNo, e);
            throw new RuntimeException("库存更新异常: " + e.getMessage(), e);
        }
    }

}