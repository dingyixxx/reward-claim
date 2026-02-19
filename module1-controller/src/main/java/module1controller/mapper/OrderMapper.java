package module1controller.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module1controller.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Select("CALL InsertOrderWithSleep( #{order.productName}, #{order.quantity},  #{order.orderNo})")
    Order insertWithSleep(@Param( "order") Order order);

    @Select("SELECT * , SLEEP(FLOOR(1 + RAND() * 5)) as sleep_time FROM `order` WHERE id = #{id}")
    Order selectByIdWithSleep(@Param("id") Long id);
}
