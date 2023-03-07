package com.scuop.userauthservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户个人认证信息
 */
@Data
@Schema(description = "用户认证信息实体")
public class UserAuth {
    // 自增id
    @Hidden
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    // 用户id 对应用户表中的id
    @Schema(description = "用户id")
    private Long userId;
    // 认证方式如：phone email
    @Schema(description = "认证方式")
    private String identityType;
    // 认证账号
    @Schema(description = "认证账号")
    private String identityfier;
    // 认证通行证
    // 如果为本地注册则为sa-256加密后的密码
    // 如果为第三方登录则保存cookie
    @Hidden
    private String credential;
    // 盐值
    @Hidden
    private String salt;

    // 对应列名
    public static final String ID = "id";
    public static final String USERID = "user_id";
    public static final String IDENTITYTYPE = "identity_type";
    public static final String IDENTITYFIER = "identityfier";
    public static final String CREDENTIAL = "credential";
    public static final String SALT = "salt";
}
