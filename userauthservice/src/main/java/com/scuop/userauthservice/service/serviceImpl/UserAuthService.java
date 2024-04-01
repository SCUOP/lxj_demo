package com.scuop.userauthservice.service.serviceImpl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scuop.imgservicefeignapi.client.ImgClient;
import com.scuop.routeservicefeignapi.client.RouteClient;
import com.scuop.userauthservice.dao.UserAuthDao;
import com.scuop.userauthservice.dao.UserDao;
import com.scuop.userauthservice.domain.User;
import com.scuop.userauthservice.domain.UserAuth;
import com.scuop.userauthservice.service.IUserAuthService;
import com.scuop.userauthservice.util.SecurityUtil;
import com.scuop.userauthservice.util.ThreadLocalUtil;
import com.scuop.userauthservice.util.UserInfo;
import com.scuop.userauthservice.util.ValidationRule;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

// TODO: 免密验证的服务没有做
@Service
@Slf4j
public class UserAuthService extends ServiceImpl<UserAuthDao, UserAuth> implements IUserAuthService {

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ValidationRule validationRule;

    @Autowired
    private RouteClient routeClient;

    @Autowired
    private ImgClient imgClient;

    /**
     * 有密码的验证
     */
    @Override
    public Long checkAccount(UserInfo userInfo) throws NullPointerException {
        // 获取盐
        String salt = getSalt(userInfo);

        QueryWrapper<UserAuth> queryWrapper = new QueryWrapper<UserAuth>();
        queryWrapper
                .select(UserAuth.USERID)
                // 账号类型和账号匹配
                .eq(UserAuth.IDENTITYTYPE, userInfo.getLoginType())
                .eq(UserAuth.IDENTITYFIER, userInfo.getAccount())
                // 密码验证
                .eq(UserAuth.CREDENTIAL, SaSecureUtil.sha256(userInfo.getPassword() + salt))
                .last("limit 1");

        UserAuth userAuth = userAuthDao.selectOne(queryWrapper);
        if (userAuth == null) {
            throw new NullPointerException("没有对应的用户或账号密码错误");
        }
        return userAuth.getUserId();
    }

    /**
     * 删除用户所有信息
     */
    // TODO: 合并处理 事务回滚未完成
    // TODO: 参照注册服务
    // TODO: 删除用户缓存
    @Override
    public boolean deleteUser(Long user_id) {
        // TODO: 删除用户 调用user服务 到DAO改SQL语句
        // TODO: 删除该用户的路线
        return (userDao.deleteById(user_id) > 0) ? true : false;
    }

    /**
     * 更新密码 盐值随密码更改更改
     */
    @Override
    public boolean updatePassword(UserInfo userInfo) {
        // 获取随机盐
        String salt = SecurityUtil.getSalt();

        UpdateWrapper<UserAuth> updateWrapper = new UpdateWrapper<UserAuth>();
        updateWrapper
                .eq("user_id", StpUtil.getLoginId())
                // 嵌套查询 满足一个条件即可
                .and(q -> {
                    for (String identityType : validationRule.getIdentityTypes()) {
                        q.eq(UserAuth.IDENTITYTYPE, identityType);
                        q.or();
                    }
                })
                // 用新的盐值更新
                .set(UserAuth.CREDENTIAL, SaSecureUtil.sha256(userInfo.getNewPassword() + salt))
                // 更新盐
                .set(UserAuth.SALT, salt);

        return userAuthDao.update(null, updateWrapper) > 0 ? true : false;
    }

    /**
     * 注册新用户
     */
    @Transactional
    public boolean register(UserInfo userInfo) {
        // 获取随机盐
        String salt = SecurityUtil.getSalt();
        User user = new User();
        // 注册后默认值为账号
        user.setNickname(userInfo.getAccount());
        if (userDao.insert(user) == 0) {
            // TODO: 外部已经检测不会出现两个同样的号 内部已经是一个事务了
            // TODO: 也许不用手动回滚了？ 算了加上保险一点
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        // 初始化用户
        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(user.getId());
        userAuth.setIdentityType(userInfo.getLoginType());
        userAuth.setIdentityfier(userInfo.getAccount());
        userAuth.setCredential(SaSecureUtil.sha256(userInfo.getPassword() + salt));
        userAuth.setSalt(salt);
        // 失败回滚
        if (userAuthDao.insert(userAuth) == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 防止重复注册
     */
    public boolean existUser(String identityType, String account) {

        // 检测是否存在当前账号
        QueryWrapper<UserAuth> queryWrapper = new QueryWrapper<UserAuth>();
        queryWrapper
                .eq(UserAuth.IDENTITYTYPE, identityType)
                .eq(UserAuth.IDENTITYFIER, account);

        return userAuthDao.exists(queryWrapper);
    }

    /**
     * 获取当前id的盐值
     */
    public String getSalt(UserInfo userInfo) throws NullPointerException {

        // 查找盐值
        QueryWrapper<UserAuth> queryWrapper = new QueryWrapper<UserAuth>();
        queryWrapper
                .select(UserAuth.SALT)
                .eq(UserAuth.IDENTITYTYPE, userInfo.getLoginType())
                .eq(UserAuth.IDENTITYFIER, userInfo.getAccount())
                .last("limit 1");

        UserAuth userAuth = userAuthDao.selectOne(queryWrapper);
        if (userAuth == null) {
            throw new NullPointerException("没有对应的用户");
        }
        return userAuth.getSalt();
    }

    /**
     * RPC删除其他信息
     * 异步优化
     * // TODO: 这里有问题，无法保证数据库事务
     * 
     * @throws IOException
     */
    @Override
    @Async
    @Transactional
    public void delOtherInfo() {
        try {
            routeClient.deleteUserAllRoutes();
            imgClient.delAllPicOfUser();
            // 注销
            String token = (String) ThreadLocalUtil.get(StpUtil.getTokenName());
            if (token == null || token.isEmpty() || token.isBlank())
                token = (String) ThreadLocalUtil.get("Cookie");
            StpUtil.logoutByTokenValue(token);
        } catch (Exception e) {
            log.error("无法成功删除用户其他信息");
            log.error(e.getMessage(), e);
            // 注销
            String token = (String) ThreadLocalUtil.get(StpUtil.getTokenName());
            if (token == null || token.isEmpty() || token.isBlank())
                token = (String) ThreadLocalUtil.get("Cookie");
            StpUtil.logoutByTokenValue(token);
        }
    }
}
