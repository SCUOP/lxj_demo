package com.scuop.userauthservice.util;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 校验规则工具
 * 扩展性强 在配置文件改规则就可扩展
 */
@ConfigurationProperties(prefix = "validation-rule")
@Data
public class ValidationRule {

    private List<String> identityTypes;

    private List<String> regex;

    /**
     * 
     * @param identityType
     * @param account
     * @return 数据校验 -1 校验失败
     */
    public int verifyType(String identityType, String account) {
        int index = identityTypes.indexOf(identityType);
        if (index > -1) {
            if (Pattern.compile(regex.get(index)).matcher(account).matches())
                return index;
        }
        return -1;
    }

    /**
     * 
     * @param password
     * @return 密码格式校验
     */
    public boolean verifyPassword(String password) {
        // 密码8-20位且至少含有一个字母和一个数字并且限制密码只能有数字和字母
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$";
        return Pattern.compile(passwordRegex).matcher(password).matches();
    }
}
