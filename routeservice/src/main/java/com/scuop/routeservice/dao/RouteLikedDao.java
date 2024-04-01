package com.scuop.routeservice.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scuop.routeservice.domain.RouteLiked;

@Repository
@Mapper
public interface RouteLikedDao extends BaseMapper<RouteLiked> {

}

