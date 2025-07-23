package com.huaweicloud.servicestage.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller
 *
 * @author provenceee
 * @since 2023-07-25
 */
@RestController
public class ProviderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderController.class);

    /**
     * 测试方法
     *
     * @param certHeader certHeader
     * @return msg
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @GetMapping("/tls-provider/hello")
    public Map<String, Object> hello(@RequestHeader("x-forwarded-client-cert") String certHeader)
            throws UnsupportedEncodingException {
        Map<String, List<String>> msg = new HashMap<>();
        String[] arr = certHeader.split(";");
        for (String str : arr) {
            String[] kv = str.split("=", 2);
            String key = kv[0];
            String value = kv[1];
            if ("Cert".equals(key)) {
                value = URLDecoder.decode(value, StandardCharsets.UTF_8.name());
            }
            if (("Cert".equals(key) || "Subject".equals(key)) && value.length() > 2) {
                value = value.substring(1, value.length() - 1);
            }
            msg.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        String cert = msg.get("Cert").get(0);
        // check client cert if you need
        LOGGER.info("cert is {}.", cert);

        Map<String, Object> map = new HashMap<>();
        map.put("cert", msg);
        return map;
    }
}
