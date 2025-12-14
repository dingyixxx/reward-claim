package com.example;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("org.example.mapper")
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,args);
    }
}