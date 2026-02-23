package com.example.sharding;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class})
@MapperScan("com.example.sharding.mapper")
public class ShardingDatabaseOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingDatabaseOrderApplication.class,args);
    }
}