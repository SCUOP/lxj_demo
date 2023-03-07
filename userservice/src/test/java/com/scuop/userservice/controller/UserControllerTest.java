package com.scuop.userservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import cn.dev33.satoken.stp.StpUtil;

@SpringBootTest
public class UserControllerTest {
    @Test
    void testChangeAvatar() {

    }

    @Test
    void testChangeNickname() {

    }

    @Test
    void testGetByName() {

    }

    @Test
    void testGetMyself() {

    }

    @Test
    void testGetOneById() {

    }

    @Test
    void testRegex() {
        String regex = "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$";
        String url = "http://www.example.com/path/to/page";

        if (url.matches(regex)) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
    }

    @Test
    void testSa() {
        try {
            System.out.println(StpUtil.getLoginIdAsLong());
        } catch (Exception e) {
            System.out.println(true);
        }

    }
}
