package com.huaweicloud.servicestage.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

    @Value("${request.ip:127.0.0.1:8092}")
    private String ip;

    @Value("${request.path:/unit-provider/hello}")
    private String path;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        PROVIDER_URL = "http://" + ip + path;
    }

    /**
     * 测试方法
     *
     * @return msg
     */
    @GetMapping("unit-consumer/hello")
    public String hello() {
        return restTemplate.getForObject(PROVIDER_URL, String.class);
    }
}
