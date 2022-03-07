package com.zjr.seckill.controller;


import com.zjr.seckill.entity.User;
import com.zjr.seckill.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zjr
 * @since 2021-12-10
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    @RequestMapping("/info")
    @ResponseBody
    public ResponseEntity<User> getInfo(User user) {
        return ResponseEntity.ok(user);
    }

//    /**
//     * 测试发送RabbitMQ消息
//     */
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq() {
//        mqSender.send("hello, RabbitMQ!");
//    }
//
//    /**
//     * fanout模式
//     * 测试发送消息
//     */
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public void mq01() {
//        mqSender.sendByFanout("hello, RabbitMQ! BY Fanout");
//    }
//
//    /**
//     * direct模式
//     * 测试发送消息
//     */
//    @RequestMapping("/mq/direct01")
//    @ResponseBody
//    public void mq02() {
//        mqSender.sendByDirectRed("hello, Red!!");
//    }
//
//    @RequestMapping("/mq/direct02")
//    @ResponseBody
//    public void mq03() {
//        mqSender.sendByDirectGreen("hello, Green!!");
//    }
//
//    /**
//     * topic模式
//     * 测试发送消息
//     */
//    @RequestMapping("/mq/topic01")
//    @ResponseBody
//    public void mq04() {
//        mqSender.sendByTopic01("hello, Red!!");
//    }
//
//    @RequestMapping("/mq/topic02")
//    @ResponseBody
//    public void mq05() {
//        mqSender.sendByTopic02("hello, Green!!");
//    }
}
