package com.scuop.routeservice.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scuop.imgservicefeignapi.client.ImgClient;
import com.scuop.routeservice.dao.RouteDao;
import com.scuop.routeservice.domain.Route;
import com.scuop.routeservice.service.IRouteService;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RouteService extends ServiceImpl<RouteDao, Route> implements IRouteService {

    @Autowired
    private RouteDao routeDao;

    @Autowired
    private ImgClient imgClient;

    /**
     * 条件模糊查询
     * 暂时按照开始时间倒序排列(越晚越前) 可能会扩展其他属性
     */
    @Override
    public List<Route> searchByConditions(
            Integer currentPage, Integer pageSize, Long id, String routeName, Long userId, String overview) {

        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        queryWrapper
                // 用户id或路线id精准查找
                .eq(id != null, Route.ID, id)
                .eq(userId != null, Route.USERID, userId)
                // 根据路线名模糊查询
                .like(routeName != null && !routeName.isBlank(), Route.ROUTENAME, routeName)
                // 根据路线概述模糊查询
                .like(overview != null && !overview.isBlank(), Route.OVERVIEW, overview)
                // 根据开始时间倒序排序 开始时间越晚越在前
                .orderByDesc(Route.STRATTIME);

        // 传入分页条件进行分页
        if (currentPage != null && pageSize != null) {
            IPage<Route> page = new Page<>(currentPage, pageSize);
            return routeDao.selectPage(page, queryWrapper).getRecords();
        }
        // 无分页条件直接查询
        return routeDao.selectList(queryWrapper);
    }

    /**
     * 根据路线的id删除路线
     * 并验证当前账户的id
     * 只有账户id和路线id一致时才可删除
     */
    @Override
    public boolean deleteByUserAndRouteId(Long id) {

        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        queryWrapper
                // 用户id和路线id需要匹配才可删除
                .eq(Route.ID, id)
                .eq(Route.USERID, StpUtil.getLoginIdAsLong());

        return routeDao.delete(queryWrapper) > 0 ? true : false;
    }

    /**
     * 根据路线的id改变路线的某些展示值
     */
    @Override
    public boolean changeById(Route route) {

        UpdateWrapper<Route> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                // 用户id和路线id需要匹配才可修改
                .eq(Route.ID, route.getId())
                .eq(Route.USERID, StpUtil.getLoginIdAsLong())
                // 若有路线名则修改路线名
                .set(route.getRouteName() != null && !route.getRouteName().isBlank(),
                        Route.ROUTENAME, route.getRouteName())
                // 若有开始时间则修改开始时间
                .set(route.getStartTime() != null, Route.STRATTIME, route.getStartTime())
                // 若有结束时间则修改结束时间
                .set(route.getEndTime() != null, Route.ENDTIME, route.getEndTime())
                // 若有概述则修改概述
                .set(route.getOverview() != null && !route.getOverview().isEmpty(),
                        Route.OVERVIEW, route.getOverview());

        return routeDao.update(null, updateWrapper) > 0 ? true : false;
    }

    /**
     * 删除当前用户的所有路线
     */
    @Override
    public boolean deleteAllRoutesOfUser() {

        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        queryWrapper
                // 匹配当前登录用户的id
                .eq(Route.USERID, StpUtil.getLoginIdAsLong());

        return routeDao.delete(queryWrapper) > 0 ? true : false;
    }

    /**
     * 异步RPC调用 删除该路线所属图片
     */
    @Override
    @Async
    public void deleteImgOfRoute(Long routeId, String token) {
        try {
            imgClient.delARouteByRouteId(routeId, token);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
