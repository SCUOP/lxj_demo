package com.scuop.userservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IUserServiceTest {
    @Autowired
    private IUserService userService;

    @Test
    public void testGet() {
        System.out.println(userService.list());
    }
}
