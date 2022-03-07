package com.zjr.seckill.service;

import com.zjr.seckill.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjr.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjr
 * @since 2021-12-10
 */
public interface IUserService extends IService<User> {

    /**
     * 根据用户名获取用户
     * @param username 用户名，可由token获取
     * @return
     */
    User selectByUsername(String username);

    /**
     * 添加一个user
     * @param user
     * @return
     */
    int addUser(User user);

    /**
     * 通过Redis中的Cookie获取User对象 (Redis中的Cookie已在login接口中配置)
     * @param userTicket
     * @return
     */
    User getUserByRedis(String userTicket,
                        HttpServletRequest request,
                        HttpServletResponse response);

    /**
     * 进行登录，生成uuid(即cookie)并存入redis中
     * @param request
     * @param response
     * @param username
     * @param password
     * @return sessionid, 即cookie
     */
    RespBean login(HttpServletRequest request, HttpServletResponse response,
                   String username, String password);

    /**
     * 用户进行变更密码操作
     * @param userTicket 用户cookie
     * @param id 用户id
     * @param password 变更后的密码(已经过一次MD5加密)
     * @return
     */
    RespBean resetPassword(String userTicket, Long id, String password,
                           HttpServletRequest request, HttpServletResponse response);
}
