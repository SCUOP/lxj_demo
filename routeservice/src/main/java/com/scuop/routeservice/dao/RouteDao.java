package com.scuop.routeservice.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scuop.routeservice.domain.Route;

@Repository
@Mapper
public interface RouteDao extends BaseMapper<Route> {

}
