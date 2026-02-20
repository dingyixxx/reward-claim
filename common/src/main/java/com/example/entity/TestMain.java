package com.example.entity;

public class TestMain {
    public static void main(String[] args) {
        InventoryMovement movement = new InventoryMovement();
        System.out.println("测试实体类创建成功: " + movement.getProductName());
        // 可加更多断言或打印
    }
}
