package com.scuop.locationservice.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scuop.locationservice.domain.Location;
import com.scuop.locationservice.service.ILocationService;
import com.scuop.locationservice.util.UserLocation;
import com.scuop.locationservice.util.ValidList;

import cn.dev33.satoken.util.SaResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 地点服务控制器
 * TODO: 未做往一条路线中间插入新的地点
 * TODO: 增删改考虑把checkPermission(location.getRouteId()重写,RPC调用速度比较慢
 * TODO: 已重写
 */
@RestController
@RequestMapping // ("/location")
@Tag(name = "地点服务")
public class LocationController {

    @Autowired
    private ILocationService locationService;

    /**
     * 
     * @param routeId
     * @return 该路线的所有路径信息
     */
    @GetMapping("getByRouteId/{route_id}")
    @Operation(summary = "获取路径信息", description = "通过传入的route_id得到这个route_id的所有地点")
    public SaResult getByRouteId(
            @PathVariable(value = "route_id") @Parameter(required = true, in = ParameterIn.PATH) Long routeId) {
        if (routeId == null)
            return SaResult.error("route_id参数不可为空");
        List<Location> oneRoute = locationService.getOneRoute(routeId);
        return SaResult.data(oneRoute);
    }

    /**
     * 核心功能 获取推荐的路书
     * 
     * @param location(当前地点)
     * @param time(当前时间)
     * @return 推荐的路书列表
     * @throws Exception
     */
    @PostMapping("/getRoutes")
    @Operation(summary = "获取推荐路书", description = "通过传入的地点列表获取多条推荐的路书")
    public SaResult getRoutes(@RequestBody @Validated ValidList<UserLocation> userLocations) throws Exception {
        return SaResult.data(locationService.getRoutesByRouteId(locationService.getRoutesId(
                userLocations.get(userLocations.size() - 1).getLocation(),
                userLocations.get(userLocations.size() - 1).getTime())));
    }

    /**
     * 向末尾添加一个地点
     * 
     * @param location
     * @return 添加地点是否成功
     */
    @PostMapping("/addLocation")
    @Operation(summary = "添加地点", description = """
            传入一个地点并添加至地点所指的路径末端
            ```
            **route_id字段必要**
            ```
            """)
    public SaResult addLocation(@RequestBody @Validated Location location) {
        if (locationService.checkPermission(location.getRouteId())) {
            location.setOrderId(locationService.getLastOrder(location.getRouteId()) + 1);
            return SaResult.data(locationService.save(location));
        }
        return SaResult.error("权限错误");
    }

    /**
     * 新增一条路线
     * 
     * @param locations
     * @return 创建路线是否成功
     */
    @PostMapping("/addLocations")
    @Operation(summary = "添加路书", description = """
            传入一个地点列表,顺序排序后添加
            ```
            **若地点的route_id不同以第一个地点为准**
            ```
            """)
    public SaResult addLocations(@RequestBody @Validated ValidList<Location> locations) {
        // 当前地点
        Long nowLocation = locations.get(0).getRouteId();
        if (nowLocation != null
                && !locationService.existRoute(nowLocation)
                && locationService.checkPermission(nowLocation)) {
            // 排序地点并以第一个地点为路线ID 防止错误数据
            locations.stream()
                    .forEach(x -> {
                        x.setOrderId(locations.indexOf(x) + 1);
                        x.setRouteId(nowLocation);
                    });
            return SaResult.data(locationService.saveBatch(locations));
        }
        return SaResult.error("不存在路线或权限错误, 无法增加地点");
    }

    /**
     * 修改一个地点
     * 
     * @param location
     * @return 修改是否成功
     */
    @PutMapping("/changeLocation")
    @Operation(summary = "修改地点", description = """
            ```
            根据传入的参数进行修改,若有些参数没有或者为空不会计入修改条件,因此可以单独修改一个值如只传入routeId orderId description三个字段来单独修改介绍
            **根据routeId和orderId修改一个地点,因此两个参数为必传参**
            ```
            """)
    public SaResult chageLocation(@RequestBody Location location) {
        if (location.getRouteId() == null)
            return SaResult.error("route_id参数不可为空");
        if (locationService.checkPermission(location.getRouteId()))
            return SaResult.data(locationService.updateByRouteOrder(location));
        return SaResult.error("权限错误");
    }

    /**
     * 删除末尾的地点
     * 
     * @param route_id
     * @return 删除是否成功
     */
    @DeleteMapping("/deleteLocation")
    @Operation(summary = "删除地点", description = "根据route_id删除这条路线的末节点")
    public SaResult deleteLocation(@RequestParam(value = "routeId") Long routeId) {
        if (routeId == null)
            return SaResult.error("route_id参数不可为空");
        if (locationService.checkPermission(routeId))
            return SaResult.data(locationService.deleteLastOrder(routeId));
        return SaResult.error("权限错误");
    }
}
