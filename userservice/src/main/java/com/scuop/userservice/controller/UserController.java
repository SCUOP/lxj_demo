package com.scuop.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scuop.userservice.domain.User;
import com.scuop.userservice.service.IUserService;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping // ("/user")
@Tag(name = "用户服务")
public class UserController {
    @Autowired
    private IUserService userService;

    // TODO: 可优化为传入一个map进行查找
    /**
     * 
     * @return 查询当前用户信息
     */
    @GetMapping("/myself")
    @Operation(summary = "查询登录用户信息, 无参数")
    // TODO: 加缓存
    public SaResult getMyself() {
        return new SaResult(200, "拉取本机用户信息", userService.getById(StpUtil.getLoginIdAsLong()));
    }

    /**
     * @deprecated 过时 可用getUser代替
     * @param id
     * @return 查询该id的用户信息
     */
    @Deprecated
    @GetMapping("/getOneUserById/{id}")
    @Operation(summary = "过时 可用getUser代替")
    // TODO: 加缓存
    public SaResult getOneUserById(@PathVariable(value = "id") Long id) {
        return new SaResult(200, "获取id为: " + id + " 的用户信息", userService.getById(id));
    }

    /**
     * TODO: 后续可扩展
     * 查询用户 可分页
     * 
     * @param currentPage 当前页数
     * @param pageSize    分页大小
     * @param id          用户id
     * @param nickname    用户名字
     * @return 查询结果
     */
    @GetMapping(value = { "/getUsers", "/getUser/{currentPage}/{pageSize}" })
    @Operation(summary = "查询用户信息", description = "可分页 根据是否存在某些参数确定是否存在条件 若要分页分页参数必填")
    public SaResult getUsers(
            @PathVariable(value = "currentPage", required = false) @Parameter(description = "分页参数,第几页", required = false, in = ParameterIn.PATH) Integer currentPage,
            @PathVariable(value = "pageSize", required = false) @Parameter(description = "分页参数,每页大小", required = false, in = ParameterIn.PATH) Integer pageSize,
            @RequestParam(value = "id", required = false) @Parameter(description = "用户的id,不传入参数或为空则没有这个条件", required = false, in = ParameterIn.QUERY) Long id,
            @RequestParam(value = "nickname", required = false) @Parameter(description = "用户的名字,模糊查询,比如1则寻找所有用户名带有1的用户,不传入参数或为空则没有这个条件", required = false, in = ParameterIn.QUERY) String nickname) {
        return SaResult.data(userService.fuzzyGetUser(currentPage, pageSize, id, nickname));
    }

    /**
     * 修改用户信息
     * 
     * @param user
     * @return 修改结果
     */
    @PutMapping("/changeUser")
    @Operation(summary = "修改用户信息", description = """
            根据是否传入某些参数决定是否修改,比如若传入的图像链接不符合要求则不会更新,只更新符合要求的字段。\n
            ```
            **另外,用户id不作为RequestBody参数传入,若传入会被忽略**
            ```
            标准传参:
            ```json
            {
                "nickname": "小吕",
                "avatar": "https://www.lhy666.com/lhy.png"
            }
            ```
                        """)
    public SaResult changeUser(@RequestBody User user) {
        return SaResult.data(userService.updateUser(user));
    }
}
