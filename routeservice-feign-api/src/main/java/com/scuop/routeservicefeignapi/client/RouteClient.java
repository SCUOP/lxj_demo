package com.scuop.routeservicefeignapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.dev33.satoken.util.SaResult;

@FeignClient(value = "routeservice")
public interface RouteClient {
    @DeleteMapping("/route/deleteUserAllRoutes")
    public SaResult deleteUserAllRoutes();

    @GetMapping("/route/checkPermission")
    @Deprecated
    public boolean checkPermission(@RequestParam(value = "id") Long id);
}
