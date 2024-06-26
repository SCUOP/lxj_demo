package com.scuop.imgservicefeignapi.client;

import java.io.IOException;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "imgservice")
public interface ImgClient {
    @DeleteMapping("/img/delAllPicOfUser")
    public void delAllPicOfUser() throws IOException;

    // TODO: 由于异步所以传入token 可以记录一下
    @DeleteMapping("/img/delARouteByRouteId")
    public void delARouteByRouteId(@RequestParam(value = "route_id") Long routeId);
}
