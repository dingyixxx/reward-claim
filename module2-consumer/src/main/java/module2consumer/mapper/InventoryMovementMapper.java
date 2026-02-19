package module2consumer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module2consumer.entity.InventoryMovement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryMovementMapper extends BaseMapper<InventoryMovement> {
}