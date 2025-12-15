package com.example.controller;


import com.example.client.IOrderClient;
import com.example.entity.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order/v1")
@Slf4j
public class OrderController implements IOrderClient {

    @GetMapping("/get-order-count/{userId}")
    @Override
    public ResponseResult getOrderCount(@PathVariable("userId") Long userId) throws InterruptedException {
//        Thread.sleep(3000);
//        根据用户的下单次数计算奖励
        log.info("用户id：{}", userId);
        return ResponseResult.okResult(8848);
    }


}
