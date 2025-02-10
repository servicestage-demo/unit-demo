package com.huaweicloud.servicestage.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * controller
 *
 * @author provenceee
 * @since 2023-07-25
 */
@RestController
public class ConsumerController {
    private static String PROVIDER_URL;

    @Value("${gateway.service.name:gateway-service}")
    private String serviceName;

    @Value("${request.path:/gateway-provider/hello}")
    private String path;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        PROVIDER_URL = "http://" + serviceName + path;
    }

    /**
     * 测试方法
     *
     * @return msg
     */
    @GetMapping("gateway-consumer/hello")
    public Map<String, Object> hello() {
        return restTemplate.getForObject(PROVIDER_URL, Map.class);
    }
}
