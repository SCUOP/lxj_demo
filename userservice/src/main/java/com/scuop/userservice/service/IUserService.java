package com.scuop.userservice.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scuop.userservice.domain.User;

public interface IUserService extends IService<User> {

    public List<User> fuzzyGetUser(Integer currentPage, Integer pageSize, Long id, String nickname);

    public boolean updateUser(User user);
}
