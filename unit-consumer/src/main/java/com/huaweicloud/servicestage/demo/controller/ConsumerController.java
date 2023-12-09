package com.huaweicloud.servicestage.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        PROVIDER_URL = "http://" + ip + "/unit-provider/hello";
    }

    /**
     * 测试方法
     *
     * @param times 持续访问时间
     * @return msg
     * @throws InterruptedException ex
     */
    @GetMapping("unit-consumer/retry")
    public String retry(@RequestParam(value = "times", defaultValue = "60") int times) throws InterruptedException {
        StringBuffer sb = new StringBuffer();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        AtomicInteger timer = new AtomicInteger();
        executorService.execute(() -> {
            int count = 1;
            while (timer.get() < times) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    String result = restTemplate.getForObject(PROVIDER_URL, String.class);
                    sb.append(count).append("|").append(LocalDateTime.now()).append("|info|").append(result);
                } catch (Exception e) {
                    sb.append(count).append("|").append(LocalDateTime.now()).append("|error|").append(e.getMessage());
                }
                sb.append(System.lineSeparator());
                count++;
            }
        });
        while (timer.getAndIncrement() < times) {
            Thread.sleep(1000);
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.sleep(100);
        }
        return sb.toString();
    }
}
