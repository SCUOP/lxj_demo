package com.scuop.userservice.service.serviceImpl;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scuop.userservice.dao.UserDao;
import com.scuop.userservice.domain.User;
import com.scuop.userservice.service.IUserService;
import com.scuop.userservice.util.RegexRule;

import cn.dev33.satoken.stp.StpUtil;

@Service
public class UserService extends ServiceImpl<UserDao, User> implements IUserService {
        @Autowired
        private UserDao userDao;

        /**
         * 用户名模糊查询
         * TODO: 返回顺序可能之后按照一个值排序
         */
        @Override
        public List<User> fuzzyGetUser(Integer currentPage, Integer pageSize, Long id, String nickname) {

                QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
                queryWrapper
                                .eq(id != null, User.ID, id)
                                .like(nickname != null && !nickname.isBlank(), User.NICKNAME, nickname);

                // 有分页
                if (currentPage != null && pageSize != null) {
                        IPage<User> page = new Page<>(currentPage, pageSize);
                        return userDao.selectPage(page, queryWrapper).getRecords();
                }
                // 无分页
                return userDao.selectList(queryWrapper);
        }

        /**
         * 更新用户信息
         */
        @Override
        public boolean updateUser(User user) {

                UpdateWrapper<User> updateWrapper = new UpdateWrapper<User>();
                updateWrapper
                                .eq(User.ID, StpUtil.getLoginIdAsLong())
                                // 用户名符合要求更新用户名
                                .set(user.getNickname() != null
                                                && !user.getNickname().isBlank()
                                                && Pattern.compile(RegexRule.NICKNAME)
                                                                .matcher(user.getNickname())
                                                                .matches(),
                                                User.NICKNAME, user.getNickname())
                                // 头像符合要求更新头像
                                .set(user.getAvatar() != null
                                                && !user.getAvatar().isBlank()
                                                && Pattern.compile(RegexRule.AVATAR)
                                                                .matcher(user.getAvatar())
                                                                .matches(),
                                                User.AVATAR, user.getAvatar());

                return userDao.update(null, updateWrapper) > 0 ? true : false;
        }

}
