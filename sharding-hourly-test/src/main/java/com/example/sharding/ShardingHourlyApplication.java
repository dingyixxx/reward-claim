package com.example.sharding;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class})
@MapperScan("com.example.sharding.mapper")
public class ShardingHourlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingHourlyApplication.class, args);
        System.out.println("=========================================");
        System.out.println("  ShardingJDBC 按小时分片测试应用启动成功！");
        System.out.println("  数据库：192.168.50.116:3306/sharding_db");
        System.out.println("  分片策略：按小时分片 (t_order_YYYYMMDDHH)");
        System.out.println("=========================================");
    }
}
