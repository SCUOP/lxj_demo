package com.scuop.userauthservice.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scuop.userauthservice.domain.User;

/**
 * 此DAO仅用于注册和删除信息
 * 此DAO不会有其他任何其他user的控制器和服务
 */
@Repository
@Mapper
public interface UserDao extends BaseMapper<User> {

}
