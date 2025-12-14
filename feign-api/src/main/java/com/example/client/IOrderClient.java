package com.example.client;

import com.example.entity.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "orders-service")
public interface IOrderClient {
    @GetMapping("/api/order/v1/get-order-count/{userId}")
    public  ResponseResult getOrderCount(@PathVariable("userId") Long userId);
}


