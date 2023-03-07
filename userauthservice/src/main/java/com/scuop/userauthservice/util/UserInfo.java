package com.scuop.userauthservice.util;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录信息 便于通过RequestBody传值
 */
@Data
@Schema(description = "用户信息DTO")
public class UserInfo {
    // 登录方式
    @NotBlank(message = "登录类型不可为空")
    @Schema(description = "登录方式")
    private String loginType;
    @NotBlank(message = "账号不可为空")
    // 账号
    @Schema(description = "账号")
    private String account;
    @NotBlank(message = "密码不可为空")
    // 密码
    @Schema(description = "密码")
    private String password;
    // 新密码
    @Schema(description = "新密码", defaultValue = "")
    private String newPassword;
}
