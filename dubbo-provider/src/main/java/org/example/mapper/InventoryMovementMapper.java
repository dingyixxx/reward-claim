package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.InventoryMovement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryMovementMapper extends BaseMapper<InventoryMovement> {
}