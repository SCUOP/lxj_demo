package com.scuop.routeservice.config;

import org.springframework.stereotype.Component;

import com.scuop.routeservice.util.ThreadLocalUtil;

import cn.dev33.satoken.stp.StpUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeginConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String tokenName = StpUtil.getTokenName();
        String token = (String) ThreadLocalUtil.get(tokenName);
        String cookie = (String) ThreadLocalUtil.get("Cookie");
        template.header(tokenName, token);
        template.header("Cookie", cookie);
    }
}
