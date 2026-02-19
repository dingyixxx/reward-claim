package module1controller.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/eureka")
@RequiredArgsConstructor
public class EurekaServiceController {

    private final DiscoveryClient discoveryClient;

    /**
     * 获取所有注册到Eureka的服务
     */
    @GetMapping("/services")
    public Map<String, Object> getAllServices() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取所有服务名称
            List<String> services = discoveryClient.getServices();

            Map<String, Object> serviceDetails = new HashMap<>();

            // 遍历每个服务获取实例信息
            for (String serviceName : services) {
                List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
                serviceDetails.put(serviceName, instances);
            }

            result.put("status", "success");
            result.put("totalServices", services.size());
            result.put("services", serviceDetails);

            log.info("获取到 {} 个服务实例", services.size());

        } catch (Exception e) {
            log.error("获取Eureka服务列表失败", e);
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 获取指定服务的实例信息
     */
    @GetMapping("/service/{serviceName}")
    public Map<String, Object> getServiceInstances(String serviceName) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

            result.put("status", "success");
            result.put("serviceName", serviceName);
            result.put("instances", instances);
            result.put("instanceCount", instances.size());

            log.info("服务 {} 有 {} 个实例", serviceName, instances.size());

        } catch (Exception e) {
            log.error("获取服务实例失败: {}", serviceName, e);
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }


}
