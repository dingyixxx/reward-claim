package com.example;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.client")
public class AwardServiceApplication {
    @Bean
    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters();
    }

    public static void main(String[] args) {
        SpringApplication.run(AwardServiceApplication.class, args);
    }

    @PostConstruct
    public void initDegradeRule() {
        System.out.println(">>> 加载熔断规则..."); // 加个日志确认
        List<DegradeRule> rules = new ArrayList<>();
//        帮我初始化 熔断 规则
        DegradeRule rule = new DegradeRule("POST:api/rewards/claim")
                .setCount(3)
                .setMinRequestAmount(6)
                .setTimeWindow(1)
                .setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);

        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }


}