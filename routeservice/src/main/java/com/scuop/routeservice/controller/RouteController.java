package com.scuop.routeservice.controller;

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

import com.scuop.routeservice.domain.Route;
import com.scuop.routeservice.service.IRouteService;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping // ("/route")
@RestController
@Tag(name = "路线服务")
public class RouteController {

    @Autowired
    private IRouteService routeService;

    /**
     * TODO: 后续可扩展
     * 查找符合条件的路线并可分页
     * 下列参数均为可选参数
     * 若有的参数为null值则不会进入查询
     * 
     * @param currentPage 当前页数
     * @param pageSize    分页大小
     * @param id          路线id
     * @param routeName   路线名
     * @param userId      用户id
     * @param overview    路线概览
     * @return 满足条件的路线列表
     */
    @GetMapping(value = { "/searchRoutes", "/searchRoutes/{currentPage}/{pageSize}" })
    @Operation(summary = "查询路线", description = """
            可分页,需分页参数,其他参数若没有或者为空则不计入查询条件
            """)
    public SaResult searchRoutes(
            @PathVariable(value = "currentPage", required = false) @Parameter(description = "分页页码", in = ParameterIn.PATH, required = false) Integer currentPage,
            @PathVariable(value = "pageSize", required = false) @Parameter(description = "分页大小(多少一页)", in = ParameterIn.PATH, required = false) Integer pageSize,
            @RequestParam(value = "id", required = false) @Parameter(description = "路线号", in = ParameterIn.QUERY, required = false) Long id,
            @RequestParam(value = "route_name", required = false) @Parameter(description = "路线名,模糊查询,会查出所有包含该字段的路线名", in = ParameterIn.QUERY, required = false) String routeName,
            @RequestParam(value = "user_id", required = false) @Parameter(description = "用户id", in = ParameterIn.QUERY, required = false) Long userId,
            @RequestParam(value = "overview", required = false) @Parameter(description = "路线概述,模糊查询,会查出所有包含该字段的路线概述", in = ParameterIn.QUERY, required = false) String overview) {
        return SaResult.data(routeService.searchByConditions(currentPage, pageSize, id, routeName, userId, overview));
    }

    /**
     * 通过路线id删除路线
     * 
     * @param id
     * @return 删除是否成功
     */
    @DeleteMapping("/deleteRoute/{id}")
    @Operation(summary = "删除一条路线", description = "根据路线id删除路线并删除所有属于该路线的地点")
    public SaResult deleteRoute(
            @PathVariable(value = "id") @Parameter(description = "路线id", in = ParameterIn.PATH, required = true) Long id) {
        // 删除的时候需要提供路线的id
        if (id != null && routeService.deleteByUserAndRouteId(id)) {
            routeService.deleteImgOfRoute(id, StpUtil.getTokenValue());

            return SaResult.ok("删除成功");
        }
        return SaResult.error("路线信息与登录id不符");
    }

    /**
     * 通过路线id修改路线
     * 
     * @param route(id必须有值)
     * @return 是否修改成功
     */
    @PutMapping("/changeRoute")
    @Operation(summary = "修改路线", description = """
            根据路线id修改路线\n
            ```
            **路线id必须有值**
            ```
            """)
    public SaResult changeRoute(@RequestBody Route route) {
        // 修改的时候需要提供路线的id
        if (route.getId() != null)
            return SaResult.data(routeService.changeById(route));
        return SaResult.error("请传入路线的id");
    }

    /**
     * 增加一条新路线
     * 
     * @param route
     * @return 返回添加成功后的路线id
     */
    @PostMapping("/addRoute")
    @Operation(summary = "新增路线", description = """
            新增一条路线\n
            ```
            **路线id赋值忽略**
            ```
            """)
    public SaResult addRoute(@RequestBody @Validated Route route) {
        // id自增 设为null防止错误输入
        route.setId(null);
        // 设为当前用户id 防止错误输入
        route.setUserId(StpUtil.getLoginIdAsLong());
        // 保存路线
        routeService.save(route);
        // 返回路线id
        return SaResult.data(route.getId());
    }

    /**
     * 删除当前用户的所有路线
     * 
     * @return 删除结果
     */
    @DeleteMapping("/deleteUserAllRoutes")
    @Operation(summary = "删除所属用户的所有路线", description = "删除当前用户所属所有路线")
    @Hidden
    public SaResult deleteUserAllRoutes(@RequestBody String token) {
        StpUtil.setTokenValue(token);
        return SaResult.data(routeService.deleteAllRoutesOfUser());
    }

    /**
     * 鉴权
     * 
     * @param id
     * @return 是否有权限
     */
    @GetMapping("/checkPermission")
    @Operation(summary = "查看权限", description = "传入用户id,查看当前用户是否为当前id")
    @Deprecated
    public boolean checkPermission(@RequestParam(value = "id") @Parameter(description = "用户id") Long id) {
        // 通过路线id查找路线
        Route route = routeService.getById(id);
        // 验证该路线是否存在且该路线的用户id是否为当前登录用户的id
        if (route != null && route.getUserId().equals(StpUtil.getLoginIdAsLong()))
            return true;
        return false;
    }
}
