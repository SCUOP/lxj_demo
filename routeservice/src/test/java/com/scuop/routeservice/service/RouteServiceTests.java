package com.scuop.routeservice.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.scuop.routeservice.domain.Route;

import cn.dev33.satoken.stp.StpUtil;

@SpringBootTest
public class RouteServiceTests {
    @Autowired
    private IRouteService routeService;

    @Test
    void testLiked() {
        StpUtil.login(18);
        boolean liked = routeService.liked(Long.valueOf(3));
        routeService.liked(Long.valueOf(6));
        routeService.liked(Long.valueOf(4));
        System.out.println(liked);
        StpUtil.logout();
    }

    @Test
    void testIsLiked() {
        StpUtil.login(18);
        assert routeService.isLiked(Long.valueOf(5)) == false;
        assert routeService.isLiked(Long.valueOf(3)) == true;
        StpUtil.logout();
    }

    @Test
    void testgetMaxLikedList() {
        StpUtil.login(18);
        List<Route> maxLikedList = routeService.getMaxLikedList("四川省成都市双流区", 3);
        assert maxLikedList.size() == 2;
        StpUtil.logout();
    }
}
