package com.scuop.userauthservice.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scuop.userauthservice.domain.UserAuth;

@Repository
@Mapper
public interface UserAuthDao extends BaseMapper<UserAuth> {

    /**
     * 
     * @param id
     * @return 删除该用户的所有数据
     *         TODO: 删除其他数据库的数据(考虑采用与UserDao相同的做法)
     */
    // @Delete("DELETE FROM user WHERE id=#{id}")
    // public Long deleteUserById(Long id);

}
