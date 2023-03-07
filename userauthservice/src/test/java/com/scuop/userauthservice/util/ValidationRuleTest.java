package com.scuop.userauthservice.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ValidationRuleTest {
    @Autowired
    private ValidationRule validationRule;

    @Test
    public void testVerifyType() {
        System.out.println(validationRule.verifyType("phone", "15808196671"));
        System.out.println(validationRule.getIdentityTypes());
        System.out.println(validationRule.getRegex());
    }
}
