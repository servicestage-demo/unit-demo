package com.huaweicloud.servicestage.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * controller
 *
 * @author provenceee
 * @since 2023-07-25
 */
@RestController
public class ProviderController {
    @Autowired
    private InetUtils inetUtils;

    @Value("${spring.application.name}")
    private String name;

    private boolean ex;

    private String ip;

    @PostConstruct
    public void init() {
        ip = inetUtils.findFirstNonLoopbackAddress().getHostAddress();
    }

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
        msg.put("ip", ip);
        Map<String, Object> map = new HashMap<>();
        map.put(name, msg);
        return map;
    }

    /**
     * 测试方法
     *
     * @param enabled enabled
     */
    @GetMapping("unit-provider/ex")
    public void ex(@RequestParam("enabled") boolean enabled) {
        ex = enabled;
    }
}
