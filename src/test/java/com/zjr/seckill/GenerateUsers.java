package com.zjr.seckill;

import com.zjr.seckill.entity.User;
import com.zjr.seckill.service.IUserService;
import com.zjr.seckill.utils.MD5Util;
import com.zjr.seckill.utils.UserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

/**
 *
 */
@SpringBootTest
public class GenerateUsers {

    @Autowired
    private IUserService userService;

    private static final int USER_NUMBER = 500;

    /**
     * 将 user0 ~ user49 用户登录，并记录下其登录cookie
     */
    @Test
    void loginSametime() {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < USER_NUMBER; i++) {
                String username = "user" + i;
                String password = "300074";
                String cookie = UserUtil.doRemoteLogin(username, password, username);
                sb.append(username).append(",").append(cookie).append("\n");
            }
            FileWriter fw = new FileWriter("D:\\桌面文件\\jmeter\\config.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString());// 往已有的文件上添加字符串
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成指定用户数，将其数据插入数据库内
     */
    @Test
    void createUser(){
        for (int i = 0; i < USER_NUMBER; i++) {
            User user = new User();
            String username = "user" + i;
            String password = "300074";
            user.setUsername(username);
            user.setPassword(MD5Util.MD5Lower(MD5Util.MD5Lower(password, username), username));
            user.setSalt(username);
            int result = userService.addUser(user);
            if (result == 1) {
                System.out.printf("用户 %s 注册成功\n", username);
            } else {
                System.out.printf("用户 %s 注册失败\n", username);
            }
        }
    }
}
