package com.example.controller;


import com.example.AppJwtUtil;
import com.example.client.IUserClient;
import com.example.entity.LoginDto;
import com.example.entity.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/user/v1")
public class UserController implements IUserClient {

    @PostMapping("/login/login_auth")
    public ResponseResult login(@RequestBody LoginDto dto) {
        System.out.println(dto);
        if ("17702142270".equals(dto.getPhone())&&"123456".equals(dto.getPassword())) {
            Map<String, Object> map = new HashMap<>();
            Long userId=1001L;
            map.put("token", AppJwtUtil.getToken(userId));
            map.put("user", userId);
            return ResponseResult.okResult(map);
        }
        return ResponseResult.errorResult(500,"用户名或密码错误");

    }




    private Integer queryUserLevelById(Long userId) {
        // 这里实现具体的查询逻辑，比如从数据库查询
        // 示例：return userService.getUserLevel(userId);
        return 12; // 默认返回等级1，实际应根据业务逻辑获取
    }

    @Override
    @GetMapping("/user-level/{userId}")
    public  ResponseResult getUserLevel(@PathVariable Long userId) throws InterruptedException {
        // 根据用户ID查询用户等级的业务逻辑
        Integer userLevel = queryUserLevelById(userId);
//        Thread.sleep(5000);
        ResponseResult result = ResponseResult.okResult(userLevel);
        return result;
    }
}
