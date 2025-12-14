package com.example.controller;

import com.example.client.IUserClient;
import com.example.entity.ResponseResult;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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

    @Value("${reward.claim.success.message:aaaa}")
    public String rewardClaimSuccessMessage;



    @PostMapping("/api/rewards/claim")
    public String claimReward(@RequestHeader("userId") String userId) throws ExecutionException, InterruptedException {
        log.info("用户id：{}", userId);
        Long userIdLong = Long.parseLong(userId);

        CompletableFuture<ResponseResult> cf = CompletableFuture.supplyAsync(() -> {
            ResponseResult userLevel = userClient.getUserLevel(userIdLong);
            System.out.println(userLevel.getData());
            return userLevel;
        });

        return cf.get().getData().toString()+"-"+rewardClaimSuccessMessage;
    }
}
