package org.example;

public interface HelloService {
    String sayHello(String name);
    public void updateInventory(Long orderId, String orderNo, String productName, Long userId, Integer quantity);
}
