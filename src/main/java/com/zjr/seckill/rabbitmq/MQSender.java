package com.zjr.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息发送者(生产者)
 */
@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀信息
     * @param msg 秒杀信息
     */
    public void sendSeckillMessage(String msg) {
        log.info("发送消息: {}", msg);
        rabbitTemplate.convertAndSend("seckill_exchange", "seckill.message", msg);
    }


//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    /**
//     * 测试整合
//     * @param msg
//     */
//    public void send(Object msg) {
//        log.info("发送消息: {}", msg);
//        rabbitTemplate.convertAndSend("queue", msg);
//    }
//
//    /**
//     * 测试Fanout模式
//     */
//    public void sendByFanout(Object msg) {
//        log.info("发送消息: {}", msg);
//        rabbitTemplate.convertAndSend("fanout_exchange", "", msg);
//    }
//
//    /**
//     * 测试direct模式
//     */
//    public void sendByDirectRed(Object msg) {
//        log.info("发送消息: {}", msg);
//        rabbitTemplate.convertAndSend("direct_exchange", "queue.red", msg);
//    }
//
//    public void sendByDirectGreen(Object msg) {
//        log.info("发送消息: {}", msg);
//        rabbitTemplate.convertAndSend("direct_exchange", "queue.green", msg);
//    }
//
//    /**
//     * 测试topic模式
//     * Q1: #.queue.#
//     * Q2: *.queue.#
//     */
//    public void sendByTopic01(Object msg) {
//        log.info("发送消息(被Q1接收): {}", msg);
//        // 只可被Q1接收
//        rabbitTemplate.convertAndSend("topic_exchange", "queue.red.message", msg);
//    }
//
//    public void sendByTopic02(Object msg) {
//        log.info("发送消息(被Q1、Q2接收): {}", msg);
//        rabbitTemplate.convertAndSend("topic_exchange", "message.queue.green.xxx", msg);
//    }
}
