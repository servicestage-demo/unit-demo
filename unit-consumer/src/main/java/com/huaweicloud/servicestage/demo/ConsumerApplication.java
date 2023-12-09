package com.huaweicloud.servicestage.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 启动类
 *
 * @author provenceee
 * @since 2023-07-25
 */
@SpringBootApplication
public class ConsumerApplication {
    /**
     * 启动方法
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class);
    }

    /**
     * restTemplate
     *
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
