package com.example.client;

import com.example.entity.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "user-service")
public interface IUserClient {
    @GetMapping("/api/user/v1/user-level/{userId}")
    public  ResponseResult getUserLevel(@PathVariable("userId") Long userId);
}


