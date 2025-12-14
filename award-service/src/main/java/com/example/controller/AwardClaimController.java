package com.example.controller;

import com.example.client.IOrderClient;
import com.example.client.IUserClient;
import com.example.entity.ResponseResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RefreshScope
@AllArgsConstructor
@Slf4j
public class AwardClaimController {
    public AwardClaimController() {}
    @Autowired
    private IUserClient userClient;

    @Autowired
    private IOrderClient orderClient;

    @Value("${reward.claim.success.message:aaaa}")
    public String rewardClaimSuccessMessage;



    @PostMapping("/api/rewards/claim")
    public String claimReward(@RequestHeader("userId") String userId) throws ExecutionException, InterruptedException {
        log.info("用户id：{}", userId);
        Long userIdLong = Long.parseLong(userId);

        CompletableFuture<ResponseResult> cf = CompletableFuture.supplyAsync(() -> {
            ResponseResult userLevel = null;
            try {
                userLevel = userClient.getUserLevel(userIdLong);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(userLevel.getData());
            return userLevel;
        });
        CompletableFuture<ResponseResult> cf2 = CompletableFuture.supplyAsync(() -> {
            ResponseResult orderCount = null;
            try {
                orderCount = orderClient.getOrderCount(userIdLong);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(orderCount.getData());
            return orderCount;
        });

        CompletableFuture<Void> cf_all = CompletableFuture.allOf(cf, cf2);
        cf_all.get();

        // 获取各个任务的结果并组装
        ResponseResult result1 = cf.get();
        ResponseResult result2 = cf2.get();

        return result1.getData().toString() + "-" + result2.getData().toString() + "-" + rewardClaimSuccessMessage;

    }
}
