package com.scuop.userauthservice.service;

import org.springframework.scheduling.annotation.Async;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scuop.userauthservice.domain.UserAuth;
import com.scuop.userauthservice.util.UserInfo;

public interface IUserAuthService extends IService<UserAuth> {

    // 有密码的验证
    public Long checkAccount(UserInfo userInfo);

    // 删除用户所有信息
    public boolean deleteUser(Long user_id);

    // 更新密码 盐值随密码更改更改
    public boolean updatePassword(UserInfo userInfo);

    // 注册新用户
    public boolean register(UserInfo userInfo);

    // 检查用户是否存在
    public boolean existUser(String identityType, String account);

    // 获取当前登录id的盐值
    public String getSalt(UserInfo userInfo);

    // 删除当前用户的其他信息
    @Async
    public void delOtherInfo(String token);
}
