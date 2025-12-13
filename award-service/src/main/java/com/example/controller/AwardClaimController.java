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
import reactor.core.publisher.Mono;

@RestController
@RefreshScope
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AwardClaimController {

    @Autowired
    private IUserClient userClient;


    @Value("${reward.claim.success.message:aaaa}")
    public String rewardClaimSuccessMessage;



    @PostMapping("/api/rewards/claim")
    public Mono<String> claimReward(@RequestHeader("userId") String userId) {
        log.info("用户id：{}", userId);
        Long userIdLong = Long.parseLong(userId);

//        从请求的headers中获取userId



        return userClient.getUserLevel(userIdLong)
                .doOnNext(userLevel -> {
                    log.info("用户等级：{}", userLevel);
                    Object data = userLevel.getData();
                })
                .thenReturn(rewardClaimSuccessMessage);

    }
}
