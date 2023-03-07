package com.scuop.locationservice.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scuop.locationservice.domain.Location;
import com.scuop.routeservicefeignapi.domain.Route;

@Repository
@Mapper
public interface LocationDao extends BaseMapper<Location> {
    @Select("SELECT * FROM route WHERE id = #{id}")
    public Route selectRouteById(Long id);
}
