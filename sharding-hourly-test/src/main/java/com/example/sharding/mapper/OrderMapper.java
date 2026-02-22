package com.example.sharding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sharding.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据时间范围查询
     */
    @Select("SELECT * FROM t_order WHERE create_time BETWEEN #{startTime} AND #{endTime}")
    List<Order> selectByTimeRange(@Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 根据用户 ID 查询
     */
    List<Order> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据订单号查询
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 聚合查询 - 总金额
     */
    @Select("SELECT SUM(amount) FROM t_order")
    BigDecimal sumAmount();

    /**
     * 聚合查询 - 总数量
     */
    @Select("SELECT COUNT(*) FROM t_order")
    Long totalCount();

    /**
     * 按小时统计订单数
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y%m%d%H') as hour, COUNT(*) as count " +
            "FROM t_order GROUP BY DATE_FORMAT(create_time, '%Y%m%d%H') ORDER BY hour")
    List<Map<String, Object>> countByHour();
}
