package com.scuop.userauthservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.scuop.userauthservice.dao.UserDao;
import com.scuop.userauthservice.util.UserInfo;
import com.scuop.userauthservice.util.ValidationRule;

@SpringBootTest
public class IUserAuthServiceTest {
    @Autowired
    private IUserAuthService userAuthService;

    @Autowired
    private ValidationRule validationRule;

    @Autowired
    private UserDao userDao;

    @Test
    void testUpdate() {
        UserInfo userInfo = new UserInfo();
        userInfo.setNewPassword("1234");
        System.out.println(userAuthService.updatePassword(userInfo));
    }

    @Test
    void testRegister() {
        UserInfo userInfo = new UserInfo();
        userInfo.setAccount("15808196671");
        userInfo.setLoginType("account");
        userInfo.setPassword("Wdl200210");
        if (
        // 账号类型和账号校验
        validationRule.verifyType(userInfo.getLoginType(), userInfo.getAccount()) != -1
                // 密码格式校验
                && validationRule.verifyPassword(userInfo.getPassword())
                // 不存在相同的用户
                && !userAuthService.existUser(userInfo.getLoginType(), userInfo.getAccount())) {
            System.out.println(userAuthService.register(userInfo));
        }
    }

    @Test
    void testDel() {
        System.out.println(userDao.deleteById(12));
    }
}
