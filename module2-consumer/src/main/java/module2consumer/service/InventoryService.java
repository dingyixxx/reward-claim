package module2consumer.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import module2consumer.entity.InventoryMovement;
import module2consumer.mapper.InventoryMovementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class InventoryService extends ServiceImpl<InventoryMovementMapper, InventoryMovement> {

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

    /**
     * 处理订单消息（兼容旧方法）
     * @param orderMessage 订单消息
     */
    @Transactional(rollbackFor = Exception.class)
    public void processOrderMessage(Object orderMessage) {
        if (orderMessage instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) orderMessage;

            Long orderId = ((Number) map.get("order_id")).longValue();
            String orderNo = (String) map.get("order_no");
            String productName = (String) map.get("product_name");
            Long userId = ((Number) map.get("user")).longValue();
            Integer quantity = ((Number) map.get("total_amount")).intValue();

            updateInventory(orderId, orderNo, productName, userId, quantity);
        }
    }
}
