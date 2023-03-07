package com.scuop.locationservice.config;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeginRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header(StpUtil.getTokenName(), StpUtil.getTokenValue());
    }
}
