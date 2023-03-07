package com.scuop.userservice.util;

/**
 * 正则规则
 */
public class RegexRule {
    // 用户名正则
    public static final String NICKNAME = "^\\S{1,12}$";
    // 头像正则
    public static final String AVATAR = "[a-zA-z]+://[^\\s]*";
}
