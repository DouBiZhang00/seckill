package com.zjr.seckill.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.zjr.seckill.entity.SeckillMessage;
import com.zjr.seckill.entity.User;
import com.zjr.seckill.entity.vo.GoodsVo;
import com.zjr.seckill.service.IGoodsService;
import com.zjr.seckill.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息接收者(消费者)
 */
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "seckill_queue")
    public void receive(String msg) {
        log.info("接收消息: {}", msg);
        SeckillMessage seckillMessage = JSON.parseObject(msg, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodId();
        User user = seckillMessage.getUser();
        // 【判断库存】
        GoodsVo goodsVo = goodsService.selectGoodsVoById(goodsId);
        if (goodsVo.getStockCount() < 1) {
            return;
        }
        // 【判断是否重复抢购】(从redis里查询，看看有没有对应记录, 有则说明重复抢购了)
        Boolean result = redisTemplate.boundSetOps("order").isMember(user.getId() + "," + goodsId);
        if (result) {
            log.warn("MQ队列接收消息时，发现用户{}尝试重复抢购商品{}，已拦截", user.getUsername(), goodsId);
            return;
        }
        // 【下单操作】
        orderService.seckill(user, goodsVo);
    }

//    /**
//     * 测试整合
//     * @param msg
//     */
//    @RabbitListener(queues = "queue")
//    public void receive(Object msg) {
//        log.info("接收消息: {}", msg);
//    }
//
//    /**
//     * 测试Fanout模式
//     * @param msg
//     */
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg) {
//        log.info("[queue01] 接收消息: {}", msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg) {
//        log.info("[queue02] 接收消息: {}", msg);
//    }
//
//    /**
//     * 测试direct模式
//     */
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg) {
//        log.info("[queue01] 接收消息: {}", msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg) {
//        log.info("[queue02] 接收消息: {}", msg);
//    }
//
//    /**
//     * 测试topic模式
//     */
//    @RabbitListener(queues = "queue_topic01")
//    public void receive05(Object msg) {
//        log.info("[queue01] 接收消息: {}", msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receive06(Object msg) {
//        log.info("[queue02] 接收消息: {}", msg);
//    }
}
