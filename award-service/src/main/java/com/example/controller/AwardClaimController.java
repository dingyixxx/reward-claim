package com.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.example.client.IOrderClient;
import com.example.client.IUserClient;
import com.example.entity.ResponseResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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


    public  String handleFallback(String userId, Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            builder.append("handleFallback---handleFallback---handleFallback---handleFallback---handleFallback---handleFallback---handleFallback---handleFallback---handleFallback---handleFallback---");
        }
        return
                builder.toString();
    }
//
//    [{"amount":1,"reward":1,"memberRewards":{"normal":1,"silver":1,"gold":1,"diamond":1,"blackDiamond":1}},{"amount":2,"reward":2,"memberRewards":{"normal":2,"silver":2,"gold":2,"diamond":2,"blackDiamond":2}}]

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Rule{
        public int amount;
        public int reward;
        public HashMap<String, Integer> memberRewards;
    }
    public  List<Rule> rules=new ArrayList<>();
    @PostMapping("/api/rewards/claim")
    @SentinelResource(value = "POST:api/rewards/claim",
            fallback = "handleFallback",
//           blockHandler = "handleBlockException",
           blockHandlerClass = AwardBlockHandler.class)
    public String claimReward(@RequestHeader("userId") String userId) throws ExecutionException, InterruptedException {
        HashMap<String, Integer> rule1_map = new HashMap<>();
        rule1_map.put("normal", 1);
        rule1_map.put("silver", 1);
        rule1_map.put("diamond", 1);
        rule1_map.put("blackDiamond", 1);
        rules.add(new Rule(1, 1, rule1_map));

        HashMap<String, Integer> rule2_map = new HashMap<>();
        rule2_map.put("normal", 2);
        rule2_map.put("silver", 2);
        rule2_map.put("blackDiamond", 2);
        rule2_map.put("diamond", 2);
        rules.add(new Rule(2, 2, rule2_map));

        if (!isAllowed(userId, 5, 30)) {
            log.info("被限流的用户id：{}", userId);
            return "The request was denied";
        }
        log.info("放行的用户id：{}", userId);
        Long userIdLong = Long.parseLong(userId);

        // 模拟可能失败的业务逻辑, 触发熔断
//        if (Math.random() > 0.8) {
//            throw new RuntimeException("模拟异常");
//        }

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

        String user_level = result1.getData().toString();
        int order_count = Integer.parseInt(result2.getData().toString());
        Rule active_rule = null;

        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).amount<order_count) {
                active_rule=rules.get(i);
            }else{
                break;
            }
        }

        System.out.println(active_rule.getMemberRewards().get(user_level));
        Integer award = active_rule.getMemberRewards().get(user_level);

        return award + "-" + rewardClaimSuccessMessage;

    }
//    @ExceptionHandler(DegradeException.class)
//    public String handleDegradeException(DegradeException ex) {
//        return "handleDegradeException...handleDegradeException...handleDegradeException...handleDegradeException..";
//    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    public boolean isAllowed(String userId, int maxRequests, int windowSeconds) {
        String key = "sliding_window:" + userId;
        long now = System.currentTimeMillis();
        long windowStart = now - windowSeconds * 1000;

        // 移除窗口外的旧记录
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        // 获取当前窗口内请求数
        Long currentCount = redisTemplate.opsForZSet().zCard(key);

        if (currentCount != null && currentCount >= maxRequests) {
            return false; // 超限
        }

        // 添加当前请求时间戳
        redisTemplate.opsForZSet().add(key, String.valueOf(now), now);
        redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);

        return true;
    }




}
