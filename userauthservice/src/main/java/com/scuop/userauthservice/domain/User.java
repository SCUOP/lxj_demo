package com.scuop.userauthservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 此处user仅用于注册 无任何服务和控制器
 * 比较差的一个设计 为了考虑事务回滚和安全性
 * 默认用户名为用户的账号
 */
@Data
@Schema(description = "用户信息实体")
public class User {
    // 自增id
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户id")
    Long id;
    // 用户名
    @Schema(description = "用户名")
    String nickname;

    // 对应列名
    public static final String ID = "id";
    public static final String NICKNAME = "nickname";
}