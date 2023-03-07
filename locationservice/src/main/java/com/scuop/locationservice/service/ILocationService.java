package com.scuop.locationservice.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scuop.locationservice.domain.Location;

public interface ILocationService extends IService<Location> {

    // 返回一条路线且以order_id增序排列
    public List<Location> getOneRoute(Long routeId);

    // 通过一个路线ID列表 返回具体路径
    public List<List<Location>> getRoutesByRouteId(List<Long> routesLong);

    // 根据条件筛选出一个路线id列表
    public List<Long> getRoutesId(String location, Integer date) throws Exception;

    // 得到该路线的最后一个地点
    public Integer getLastOrder(Long routeId);

    // 根据路线id和地点id更新地点信息
    public boolean updateByRouteOrder(Location location);

    // 删去某路线的末地点
    public boolean deleteLastOrder(Long route_id);

    // 查看是否存在这条路线
    public boolean existRoute(Long route_id);

    public boolean checkPermission(Long routeId);
}
