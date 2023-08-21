package com.huaweicloud.servicestage.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * controller
 *
 * @author provenceee
 * @since 2023-07-25
 */
@RestController
public class ConsumerController {
    private static final String PROVIDER_URL = "http://unit-provider/unit-provider/hello";

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.cloud.servicecomb.discovery.datacenter.name}")
    private String datacenterName;

    @Value("${spring.cloud.servicecomb.discovery.datacenter.region}")
    private String region;

    @Value("${spring.cloud.servicecomb.discovery.datacenter.availableZone}")
    private String availableZone;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 测试方法
     *
     * @return msg
     */
    @GetMapping("unit-consumer/hello")
    public Map<String, Object> hello() {
        Map<String, String> msg = new HashMap<>();
        msg.put("datacenterName", datacenterName);
        msg.put("region", region);
        msg.put("availableZone", availableZone);
        Map<String, Object> map = new HashMap<>(restTemplate.getForObject(PROVIDER_URL, Map.class));
        map.put(name, msg);
        return map;
    }
}
