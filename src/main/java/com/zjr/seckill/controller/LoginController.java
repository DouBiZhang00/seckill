package com.zjr.seckill.controller;

import com.zjr.seckill.entity.User;
import com.zjr.seckill.service.IUserService;
import com.zjr.seckill.utils.CookieUtil;
import com.zjr.seckill.utils.MD5Util;
import com.zjr.seckill.vo.RespBean;
import com.zjr.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 进行登录操作
     * @param username 前端用户名
     * @param password 前端密码(md5加密)
     * @return json数据
     */
    @RequestMapping("/tologin")
    @ResponseBody
    public ResponseEntity<RespBean> loginUser(@RequestParam("username") String username,
                                              @RequestParam("password") String password,
                                              HttpServletRequest request, HttpServletResponse response){
        /*
         *  前端登录：密码【300074】，加盐加密后【MD5(300074zjr)】= 168010e8df123f16ae8c6bdbaee300fd
         *  后端加盐：再次使用MD5加密，然后存入数据库（指注册的时候）。
         */
        log.info("获取前端参数=====>" + username + ", " + password);
        return ResponseEntity.ok(userService.login(request, response, username, password));
    }

    /**
     * 进行注销操作，个人理解是把redis中用户对应的session删除
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        String cookie = CookieUtil.getCookieValue(request, "userTicket");
        Boolean result = redisTemplate.delete("user:" + cookie);
        log.info("注销的结果: {}", result);
        return "redirect:/login";
    }

    /**
     * 进行注册操作
     */
    @RequestMapping("/toregister")
    @ResponseBody
    public ResponseEntity<RespBean> register(@RequestParam("username") String username,
                                            @RequestParam("password") String password) {
        // 以用户名作为后台的"盐"
        String password2 = MD5Util.MD5Lower(password, username);
        // 用户信息写入数据库
        User user = new User();
        user.setSalt(username);
        user.setUsername(username);
        user.setPassword(password2);
        // 没有这个用户，才能进行插入操作
        if(userService.selectByUsername(username) == null) {
            int result = userService.addUser(user);
            if (result == 1) {
                log.info("用户:{} 注册成功!", username);
                return ResponseEntity.ok(RespBean.success());
            } else {
                return ResponseEntity.ok(RespBean.error(RespBeanEnum.ERROR));
            }
        }
        return ResponseEntity.ok(RespBean.error(RespBeanEnum.REGISTER_ERROR));
    }

}
