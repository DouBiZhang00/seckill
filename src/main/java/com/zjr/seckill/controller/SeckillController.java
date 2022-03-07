package com.zjr.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.wf.captcha.ArithmeticCaptcha;
import com.zjr.seckill.entity.SeckillMessage;
import com.zjr.seckill.entity.User;
import com.zjr.seckill.entity.vo.GoodsVo;
import com.zjr.seckill.rabbitmq.MQSender;
import com.zjr.seckill.service.IGoodsService;
import com.zjr.seckill.service.IOrderService;
import com.zjr.seckill.service.ISeckillOrderService;
import com.zjr.seckill.vo.RespBean;
import com.zjr.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀
 */
@Controller
@RequestMapping("/seckill")
@Slf4j
public class SeckillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    /**
     * 秒杀主接口
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/toSeckill", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RespBean> toSeckill(@PathVariable String path, User user, Long goodsId) {
        if (user == null) {
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.NOT_LOGIN_ERROR));
        }
        // 检查用户是否获取了秒杀地址
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            log.info("用户{} 没有获取到该秒杀地址/{}/toSeckill, 校验失败", user.getUsername(), path);
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.REQUEST_ILLEGAL));
        }
        // 【判断是否重复抢购】(从redis里查询，看看有没有对应记录, 有则说明重复抢购了)
        Boolean result = redisTemplate.boundSetOps("order").isMember(user.getId() + "," + goodsId);
        if (result) {
            log.warn("用户{}尝试重复抢购商品{}，但被拦截", user.getUsername(), goodsId);
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.REPEAT_ERROR));
        }
        // 【判断是否空库存】利用map标记，减少以下redis预减库存时的判断
        if (emptyStockMap.get(goodsId)) {
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.EMPTY_STOCK));
        }
        // 【预减库存】获取(经过Redis方法)递减之后的库存
        Long stock = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            // 当Redis库存为0时，stock=-1，此时会进入这个判断，为了明确知道redis库存为0，将其递增一次
            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
            emptyStockMap.put(goodsId, true);
            log.info(emptyStockMap.toString());
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.EMPTY_STOCK));
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        // 异步秒杀
        mqSender.sendSeckillMessage(JSON.toJSONString(seckillMessage));
        // 0: 排队中
        return ResponseEntity.ok(RespBean.success(0));
    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId  [not null]: 成功  [-1]: 秒杀失败  [0]: 排队中
     */
    @GetMapping("/result")
    @ResponseBody
    public ResponseEntity<RespBean> getResult(User user, Long goodsId) {
        if (user == null) {
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.NOT_LOGIN_ERROR));
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return ResponseEntity.ok(RespBean.success(orderId));
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("/path")
    @ResponseBody
    public ResponseEntity<RespBean> getSeckillPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null) {
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.NOT_LOGIN_ERROR));
        }
        // 利用计数达到接口限流效果, 30秒内如果访问超过5次，则限流
        String uri = request.getRequestURI();
        String key = uri + ":" + user.getId();
        Integer cnt = (Integer) redisTemplate.opsForValue().get(key);
        if (cnt == null) {
            redisTemplate.opsForValue().set(key, 1, 30, TimeUnit.SECONDS);
        } else if (cnt < 5) {
            redisTemplate.opsForValue().increment(key);
        } else {
            log.info("访问过于频繁，请稍后再逝世");
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.ACCESS_LIMIT));
        }
        // 校验验证码
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            log.info("验证码校验错误! ");
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.CAPTCHA_ERROR));
        }
        String path = orderService.createPath(user, goodsId);
        return ResponseEntity.ok(RespBean.success(path));
    }

    @GetMapping("/captcha")
    public void verifyCodde(User user, Long goodsId, HttpServletResponse response) throws IOException {
        if (user == null) {
            log.info("{}", RespBeanEnum.NOT_LOGIN_ERROR.getMessage());
        }
        // 设置请求头为输出图片(流)的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 生成验证码，将结果放入Redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + "," + goodsId,
                captcha.text(), 300, TimeUnit.SECONDS);
        log.info("captcha.text() = {}", captcha.text());
        captcha.out(response.getOutputStream());
    }

    /**
     * 系统初始化，把商品库存预加载到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.selectGoodsVo();
        // 首先确保有相应的商品
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 把商品库存存入Redis
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
