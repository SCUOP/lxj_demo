package com.scuop.userauthservice.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scuop.userauthservice.service.IUserAuthService;
import com.scuop.userauthservice.util.UserInfo;
import com.scuop.userauthservice.util.ValidationRule;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 用户认证控制器
 * TODO: 免密登录的控制器和服务没有做
 */
@RequestMapping // ("/userauth")
@RestController
@Tag(name = "用户认证服务")
public class UserAuthController {

    @Autowired
    private IUserAuthService userAuthService;

    @Autowired
    private ValidationRule validationRule;

    /**
     * 
     * @param UserInfo
     * @return 用户登录成功
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = """
            用户账号密码登录\n
            ```
            **不需传入新密码字段,若传入会被忽略**
            ```
            """)
    public SaResult login(@RequestBody @Validated UserInfo userInfo) {
        // 检查账号
        Long user_id = userAuthService.checkAccount(userInfo);
        // 登录
        StpUtil.login(user_id);
        return new SaResult(200, "登录成功", StpUtil.getTokenInfo());
    }

    /**
     * 
     * @return 退出登录
     */
    @GetMapping("/logout")
    @Operation(summary = "注销", description = "退出登录")
    public SaResult logout() {
        // 退出登录
        StpUtil.logout();
        return SaResult.data("true");
    }

    /**
     * 
     * @return 登录状态
     */
    @GetMapping("/islogin")
    @Operation(summary = "检测登录状态")
    public SaResult isLogin() {
        return SaResult.data(StpUtil.isLogin());
    }

    // TODO: 增删改数据校验非常重要此处为密码校验删除 免密登录之后再做
    /**
     * TODO: 二级验证
     * 
     * @param userInfo
     * @return 删除是否成功
     * @throws IOException
     */
    @DeleteMapping("/deleteUser")
    @Operation(summary = "删除用户", description = """
            根据用户账号密码删除\n
            ```
            **不需传入新密码字段,若传入会被忽略**
            ```
            """)
    public SaResult deleteUser(@RequestBody @Validated UserInfo userInfo) throws IOException {
        // TODO: 删除账号的所有有关信息, 包括该账号对应的账号和路线
        Long user_id = userAuthService.checkAccount(userInfo);
        // 当前token对应账号一致才可删除
        if (user_id.equals(StpUtil.getLoginIdAsLong())) {
            if (userAuthService.deleteUser(user_id)) {
                // TODO: 删除用户的其他信息
                userAuthService.delOtherInfo(StpUtil.getTokenValue());
                return SaResult.ok("删除成功");
            }
        }
        return SaResult.error("操作错误, 请检查数据");
    }

    /**
     * 更新密码 由于更改密码后需重新登录 不更新盐值
     * 
     * @param userInfo
     * @return 更新密码的结果
     */
    @PutMapping("/updatePassword")
    @Operation(summary = "更新用户", description = """
            ```
            **更新密码,需传入新密码**
            ```
            """)
    public SaResult updatePassword(@RequestBody @Validated UserInfo userInfo) {
        // 数据校验
        if (userInfo.getNewPassword() != null && validationRule.verifyPassword(userInfo.getNewPassword())) {
            try {
                userAuthService.checkAccount(userInfo);
            } catch (Exception e) {
                throw new NullPointerException("账号信息错误");
            }
            if (userAuthService.updatePassword(userInfo)) {
                StpUtil.logout();
                return SaResult.ok("修改成功");
            }
        }
        return SaResult.error("修改失败, 请检查数据");
    }

    /**
     * 用户注册
     * 
     * @param userInfo
     * @return 用户注册
     */
    // TODO: 新用户注册!!
    @PostMapping("/register")
    @Operation(summary = "注册用户", description = """
            创建新用户\n
            ```
            **不需传入新密码字段,若传入会被忽略**
            ```
            """)
    public SaResult register(@RequestBody @Validated UserInfo userInfo) {
        // 校验注册形式
        if (
        // 账号类型和账号校验
        validationRule.verifyType(userInfo.getLoginType(), userInfo.getAccount()) != -1
                // 密码格式校验
                && validationRule.verifyPassword(userInfo.getPassword())
                // 不存在相同的用户
                && !userAuthService.existUser(userInfo.getLoginType(), userInfo.getAccount())) {
            if (userAuthService.register(userInfo)) {
                return SaResult.ok("注册成功");
            }
        }
        return SaResult.error("注册失败");
    }
}
