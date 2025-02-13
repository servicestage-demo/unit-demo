package com.huaweicloud.servicestage.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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

    /**
     * 测试方法
     *
     * @return msg
     */
    @PostMapping("ex-processor-provider/hello")
    public Map<String, Object> hello(HttpServletResponse response, @RequestBody User user) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("user", user);
        Map<String, Object> map = new HashMap<>();
        map.put(name, msg);
        response.setHeader("aaaa", "bbbb");
        response.setHeader("remove-resp-key", "remove-resp-key");
        response.setHeader("add-resp-key", "aaaaa");
        response.setHeader("set-resp-key", "set-resp-value-test");
        return map;
    }

    public static class User {
        private String name;

        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
