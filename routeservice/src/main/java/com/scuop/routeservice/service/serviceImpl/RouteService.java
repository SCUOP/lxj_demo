package com.scuop.routeservice.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scuop.imgservicefeignapi.client.ImgClient;
import com.scuop.routeservice.dao.RouteDao;
import com.scuop.routeservice.dao.RouteLikedDao;
import com.scuop.routeservice.domain.Route;
import com.scuop.routeservice.domain.RouteLiked;
import com.scuop.routeservice.service.IRouteService;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RouteService extends ServiceImpl<RouteDao, Route> implements IRouteService {

    @Autowired
    private RouteDao routeDao;

    @Autowired
    private RouteLikedDao routeLikedDao;

    @Autowired
    private ImgClient imgClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 点赞
    // 返回值-1取消点赞，0点赞失败，1点赞成功
    @Override
    // TODO: 异步落库优化
    @Transactional
    public boolean liked(Long routeId) {
        Long userId = StpUtil.getLoginIdAsLong();
        String key = "route:liked:" + routeId;
        Boolean exsitRedis = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
        if (BooleanUtils.isFalse(exsitRedis)) {
            Route route = routeDao.selectById(routeId);
            if (route != null) {
                boolean isSuccess = routeLikedDao.insert(new RouteLiked(null, userId, routeId)) > 0;
                if (isSuccess) {
                    int likedCount = route.getLikedCount() + 1;
                    route.setLikedCount(likedCount);
                    if (routeDao.updateById(route) > 0) {
                        stringRedisTemplate.opsForSet().add(key, userId.toString());
                        String countKey = "route:likedcount:" + route.getArea();
                        stringRedisTemplate.opsForZSet().add(countKey, routeId.toString(), Double.valueOf(likedCount));
                        return true;
                    }
                } else {
                    int likedCount = route.getLikedCount() - 1;
                    if (routeDao.deleteById(route) > 0) {
                        String countKey = "route:likedcount:" + route.getArea();
                        stringRedisTemplate.opsForZSet().add(countKey, routeId.toString(), Double.valueOf(likedCount));
                        return true;
                    }
                }
            }
        } else {
            boolean isSuccess = routeLikedDao.delete(
                    new QueryWrapper<RouteLiked>().eq(RouteLiked.ROUTEID, routeId).eq(RouteLiked.USERID, userId)) > 0;
            Route route;
            if (isSuccess) {
                route = routeDao.selectById(routeId);
                if (route != null) {
                    int likedCount = route.getLikedCount() - 1;
                    route.setLikedCount(likedCount);
                    if (routeDao.updateById(route) > 0) {
                        stringRedisTemplate.opsForSet().remove(key, userId.toString());
                        String countKey = "route:likedcount:" + route.getArea();
                        stringRedisTemplate.opsForZSet().add(countKey, routeId.toString(), Double.valueOf(likedCount));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    // TODO: 因为没有异步落库，实际上就是缓存
    public boolean isLiked(Long routeId) {
        Long userId = StpUtil.getLoginIdAsLong();
        String key = "route:liked:" + routeId;
        Boolean exsitRedis = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
        if (BooleanUtils.isFalse(exsitRedis)) {
            RouteLiked routeLiked = routeLikedDao.selectOne(
                    new QueryWrapper<RouteLiked>().eq(RouteLiked.ROUTEID, routeId).eq(RouteLiked.USERID,
                            userId));
            if (routeLiked != null)
                return true;
        } else
            return true;
        return false;
    }

    // TODO: 获取点赞排名最前的N个路书，该服务应与路书推荐算法服务结合使用
    public List<Route> getMaxLikedList(String area, int count) {
        if (count < 1)
            count = 1;
        String key = "route:likedcount:" + area;
        Set<String> routeIdSet = stringRedisTemplate.opsForZSet().reverseRange(key, 0, count - 1);
        List<Route> likedList = new ArrayList<>();
        if (routeIdSet != null && routeIdSet.size() != 0)
            likedList = routeDao.selectBatchIds(routeIdSet);
        else {
            likedList = routeDao.selectList(new QueryWrapper<Route>().eq(Route.AREA, area).orderByDesc(Route.LIKEDCOUNT)
                    .last("limit " + count));
        }
        return likedList;
    }

    /**
     * 条件模糊查询
     * 暂时按照开始时间倒序排列(越晚越前) 可能会扩展其他属性
     */
    // TODO: 缓存
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
    // TODO: 删除缓存
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
    // TODO: 更新缓存
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
    // TODO: 删除缓存
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
    public void deleteImgOfRoute(Long routeId) {
        try {
            imgClient.delARouteByRouteId(routeId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
