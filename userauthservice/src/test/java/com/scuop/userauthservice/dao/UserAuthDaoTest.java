package com.scuop.userauthservice.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.scuop.userauthservice.domain.UserAuth;

@SpringBootTest
public class UserAuthDaoTest {
    @Autowired
    private UserAuthDao userAuthDao;

    @Test
    public void testInsert() {
        UserAuth userAuth = new UserAuth();
        Long user_id = (long) 1;
        userAuth.setUserId(user_id);
        userAuth.setIdentityType("test");
        userAuth.setIdentityfier("test");
        userAuth.setCredential("test");
        System.out.println(userAuthDao.insert(userAuth));
        System.out.println(userAuth.getId());
    }

    @Test
    public void testRegisterUser() {
        
    }
}
