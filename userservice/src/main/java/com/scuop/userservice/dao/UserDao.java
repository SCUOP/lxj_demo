package com.scuop.userservice.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scuop.userservice.domain.User;

@Repository
@Mapper
public interface UserDao extends BaseMapper<User> {

}
