package com.scuop.routeservice.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scuop.routeservice.domain.Route;

public interface IRouteService extends IService<Route> {
    // 条件查询
    public List<Route> searchByConditions(Integer currentPage, Integer pageSize,
            Long id, String routeName, Long userId, String overview);

    // 根据路线id删除路线
    public boolean deleteByUserAndRouteId(Long id);

    // 根据路线id改变路线
    public boolean changeById(Route route);

    // 删除当前登录用户的所有路线
    public boolean deleteAllRoutesOfUser();

    // 删除该路线的图片
    @Async
    public void deleteImgOfRoute(Long routeId, String string);
}
