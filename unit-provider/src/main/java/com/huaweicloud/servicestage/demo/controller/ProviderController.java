package com.huaweicloud.servicestage.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Value("${spring.cloud.servicecomb.discovery.version}")
    private String version;

    @Value("${SERVICECOMB_INSTANCE_PROPS:}")
    private String props;

    /**
     * 测试方法
     *
     * @return msg
     */
    @GetMapping("unit-provider/hello")
    public Map<String, Object> hello() {
        Map<String, String> msg = new HashMap<>();
        msg.put("SERVICECOMB_INSTANCE_PROPS", props);
        msg.put("version", version);
        Map<String, Object> map = new HashMap<>();
        map.put(name, msg);
        return map;
    }
}
