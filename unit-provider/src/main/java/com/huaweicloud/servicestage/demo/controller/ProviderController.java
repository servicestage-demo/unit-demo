package com.huaweicloud.servicestage.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * controller
 *
 * @author provenceee
 * @since 2023-07-25
 */
@RestController
public class ProviderController {
    /**
     * 测试方法
     *
     * @param request request
     * @param response response
     * @return msg
     */
    @GetMapping("/hello")
    public Map<String, Object> hello(HttpServletRequest request, HttpServletResponse response) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, List<Object>> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            if ("cookie".equalsIgnoreCase(key)) {
                continue;
            }
            Enumeration<String> values = request.getHeaders(key);
            List<Object> list = new ArrayList<>();
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                list.add(value);
                if (key.toLowerCase(Locale.ROOT).startsWith("cookie")) {
                    Cookie cookie = new Cookie(key, value);
                    cookie.setPath(request.getRequestURI());
                    response.addCookie(cookie);
                }
            }
            headers.put(key, list);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("cookies", request.getCookies());
        map.put("headers", headers);
        return map;
    }
}
