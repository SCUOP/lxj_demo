package com.scuop.userauthservice.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {
    /**
     * 
     * @return 随机盐值
     */
    public static String getSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[32];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
