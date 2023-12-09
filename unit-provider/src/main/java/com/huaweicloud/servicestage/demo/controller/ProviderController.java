package com.huaweicloud.servicestage.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * controller
 *
 * @author provenceee
 * @since 2023-07-25
 */
@RestController
public class ProviderController {
    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.cloud.servicecomb.discovery.datacenter.name}")
    private String datacenterName;

    @Value("${spring.cloud.servicecomb.discovery.datacenter.region}")
    private String region;

    @Value("${spring.cloud.servicecomb.discovery.datacenter.availableZone}")
    private String availableZone;

    private boolean ex;

    /**
     * 测试方法
     *
     * @return msg
     * @throws Exception ex
     */
    @GetMapping("unit-provider/hello")
    public Map<String, Object> hello() throws Exception {
        if (ex) {
            throw new Exception("exception");
        }
        Map<String, String> msg = new HashMap<>();
        msg.put("datacenterName", datacenterName);
        msg.put("region", region);
        msg.put("availableZone", availableZone);
        Map<String, Object> map = new HashMap<>();
        map.put(name, msg);
        return map;
    }

    /**
     * 测试方法
     */
    @GetMapping("unit-provider/ex")
    public void ex(@RequestParam("enabled") boolean enabled) {
        ex = enabled;
    }
}
