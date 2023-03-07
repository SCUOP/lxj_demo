package com.scuop.locationservice.service.serviceImpl;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scuop.locationservice.dao.LocationDao;
import com.scuop.locationservice.domain.Location;
import com.scuop.locationservice.service.ILocationService;
import com.scuop.locationservice.util.RegexRule;
import com.scuop.routeservicefeignapi.domain.Route;

import cn.dev33.satoken.stp.StpUtil;

@Service
public class LocationService extends ServiceImpl<LocationDao, Location> implements ILocationService {

        @Autowired
        private LocationDao locationDao;

        /**
         * 返回一条路线且以order_id增序排列
         */
        @Override
        public List<Location> getOneRoute(Long routeId) {

                QueryWrapper<Location> queryWrapper = new QueryWrapper<Location>();
                queryWrapper
                                // 根据路线id搜索 顺序id增序排列
                                .eq(Location.ROUTEID, routeId)
                                .orderByAsc(Location.ORDERID);

                return locationDao.selectList(queryWrapper);
        }

        /**
         * 通过一个路线ID列表 返回具体路径
         */
        @Override
        public List<List<Location>> getRoutesByRouteId(List<Long> routes) {

                QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
                queryWrapper
                                // 在routes列表的所有route_id中查找
                                .in(Location.ROUTEID, routes)
                                .orderByAsc(Location.ROUTEID, Location.ORDERID);

                // 每个路线分开
                List<List<Location>> routesList = locationDao.selectList(queryWrapper)
                                .stream()
                                .collect(Collectors.groupingBy(Location::getRouteId))
                                .values()
                                .stream()
                                .collect(Collectors.toList());
                return routesList;
        }

        /**
         * 根据条件筛选出一个路线列表
         * 
         * @throws Exception
         */
        @Override
        public List<Long> getRoutesId(String location, Integer date) throws Exception {

                QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
                queryWrapper
                                // 名字
                                .eq(Location.LOCATIONNAME, location)
                                // 大于当前的前两个小时
                                .ge(Location.STRATTIME, date - 180)
                                // 小于当前的后两个小时
                                .le(Location.ENDTIME, date + 180)
                                .groupBy(Location.ROUTEID)
                                .select(Location.ROUTEID);

                List<Location> locations = locationDao.selectList(queryWrapper);

                // 没有查找到
                if (locations.isEmpty())
                        throw new Exception("未查询到符合条件的路线");
                // 返回路线id列表
                return locations.stream()
                                .map(route -> route.getRouteId())
                                .toList();
        }

        /**
         * 得到该路线的最后一个地点
         */
        @Override
        public Integer getLastOrder(Long routeId) {

                QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
                queryWrapper
                                .eq(Location.ROUTEID, routeId)
                                .select(Location.ORDERID)
                                .orderByDesc(Location.ORDERID)
                                .last("limit 1");

                return locationDao.selectOne(queryWrapper).getOrderId();
        }

        /**
         * 根据路线id和地点id更新地点信息
         */
        @Override
        public boolean updateByRouteOrder(Location location) {

                UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
                updateWrapper
                                .eq(Location.ROUTEID, location.getRouteId())
                                .eq(Location.ORDERID, location.getOrderId())
                                .set(location.getStartTime() != null, Location.STRATTIME, location.getStartTime())
                                .set(location.getEndTime() != null, Location.ENDTIME, location.getEndTime())
                                .set(location.getLocationName() != null && !location.getLocationName().isBlank(),
                                                Location.LOCATIONNAME,
                                                location.getLocationName())
                                // 介绍不为空
                                .set(location.getDescription() != null
                                                && !location.getDescription().isBlank(),
                                                Location.DESCRIPTION, location.getDescription())
                                // 图像链接满足url格式
                                .set(location.getImgUrl() != null && !location.getImgUrl().isBlank()
                                                && Pattern.compile(RegexRule.IMGURL).matcher(location.getImgUrl())
                                                                .matches(),
                                                Location.IMGURL, location.getImgUrl());

                return locationDao.update(null, updateWrapper) > 0 ? true : false;
        }

        /**
         * 删去某路线的末地点
         */
        @Override
        public boolean deleteLastOrder(Long route_id) {

                QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
                queryWrapper
                                .eq(Location.ROUTEID, route_id)
                                .orderByDesc(Location.ORDERID)
                                .last("limit 1");

                return locationDao.delete(queryWrapper) > 0 ? true : false;
        }

        /**
         * 查看是否存在这条路线
         */
        @Override
        public boolean existRoute(Long route_id) {

                QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
                queryWrapper
                                .eq(Location.ROUTEID, route_id);

                return locationDao.exists(queryWrapper);
        }

        @Override
        public boolean checkPermission(Long routeId) {
                Route route = locationDao.selectRouteById(routeId);
                // 验证该路线是否存在且该路线的用户id是否为当前登录用户的id
                if (route != null && route.getUserId().equals(StpUtil.getLoginIdAsLong()))
                        return true;
                return false;
        }

}
