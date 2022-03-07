package com.zjr.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjr.seckill.entity.User;
import com.zjr.seckill.mapper.UserMapper;
import com.zjr.seckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjr.seckill.utils.CookieUtil;
import com.zjr.seckill.utils.MD5Util;
import com.zjr.seckill.utils.UUIDUtil;
import com.zjr.seckill.vo.RespBean;
import com.zjr.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zjr
 * @since 2021-12-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 进行登录，生成uuid(即cookie)并存入redis中
     * @return sessionid, 即cookie
     */
    @Override
    public RespBean login(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        // 判断用户名是否存在
        User user = selectByUsername(username);
        if (user == null) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        // 验证密码, 通过MD5加密后向数据库验证
        String password2 = MD5Util.MD5Lower(password, user.getSalt());
        if (!password2.equals(user.getPassword())) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        // 生成cookie, 存入redis
        String ticket = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    /**
     * 用户进行变更密码操作
     *
     * @param userTicket 用户cookie
     * @param id 用户id
     * @param password 变更后的密码(已经过一次MD5加密)
     * @return
     */
    @Override
    public RespBean resetPassword(String userTicket, Long id, String password,
                                  HttpServletRequest request, HttpServletResponse response) {
        // 根据cookie获取用户对象
        User user = getUserByRedis(userTicket, request, response);
        if (user == null) {
            return RespBean.error(RespBeanEnum.USER_NOT_EXIST);
        }
        user.setPassword(MD5Util.MD5Lower(password, user.getSalt()));
        int result = userMapper.updateById(user);
        if (result == 1) {
            // 清除redis的用户cookie信息
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.UPDATE_PASSWORD_ERROR);
    }

    @Override
    public User selectByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public int addUser(User user) {
        return userMapper.insert(user);
    }

    @Override
    public User getUserByRedis(String userTicket,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        // 从Redis获取用户信息
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        // 用户不为空，重新设置以下cookie (可能是防止用户信息消失吧)
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

}
