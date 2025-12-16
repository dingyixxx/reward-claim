package com.example.controller;

import com.alibaba.csp.sentinel.slots.block.BlockException;

public class AwardBlockHandler {
    // 添加异常处理方法
    public static String handleBlockException(String userId, BlockException ex) {
        return
                "Requests are too frequent, please try again later";
    }



}
