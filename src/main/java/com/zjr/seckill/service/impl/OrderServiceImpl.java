package com.zjr.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjr.seckill.entity.Order;
import com.zjr.seckill.entity.SeckillGoods;
import com.zjr.seckill.entity.SeckillOrder;
import com.zjr.seckill.entity.User;
import com.zjr.seckill.entity.vo.GoodsVo;
import com.zjr.seckill.entity.vo.OrderDetailVo;
import com.zjr.seckill.mapper.OrderMapper;
import com.zjr.seckill.service.IGoodsService;
import com.zjr.seckill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjr.seckill.service.ISeckillGoodsService;
import com.zjr.seckill.service.ISeckillOrderService;
import com.zjr.seckill.utils.MD5Util;
import com.zjr.seckill.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zjr
 * @since 2021-12-20
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 秒杀操作，生成秒杀订单, 是一个事务
     *
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        // 从秒杀的库存中取出
        SeckillGoods seckillGoods = seckillGoodsService.getOne(
                new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId())
        );
        // 先判断库存是否>0，判断通过才可以秒杀，否则可能导致超卖
        if (seckillGoods.getStockCount() <= 0) {
            // 将当前库存为空的商品，存入redis，方便判断
            redisTemplate.boundSetOps("isStockEmpty").add(goods.getId());
            return null;
        }
        // 对应秒杀商品库存数减一
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.updateById(seckillGoods);
        if (!result) {
            log.warn("网络拥挤，用户尝试秒杀失败(因为有乐观锁的存在)");
            return null;
        }
        log.info("幸运儿{} 成功拿到秒杀资格", user.getUsername());
        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        // 用户id+商品id信息存入redis(的set集合内)，判断是否重复购买时就不用再向数据库查询
        redisTemplate.boundSetOps("order").add(user.getId() + "," + goods.getId());
//        redisTemplate.boundSetOps("order").expire(60, TimeUnit.MINUTES);
        return order;
    }

    /**
     * 订单详情，根据订单id获取详情
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVo selectOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.selectGoodsVoById(order.getGoodsId());
        OrderDetailVo orderVo = new OrderDetailVo();
        orderVo.setOrder(order);
        orderVo.setGoodsVo(goodsVo);
        return orderVo;
    }

    /**
     * 获取秒杀地址
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String url = MD5Util.MD5Lower(UUIDUtil.uuid() + "123456");
        // 设置这个秒杀地址生效时间，过了这个生效时间就需要重新获取。
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, url, 60, TimeUnit.SECONDS);
        return url;
    }

    /**
     * 校验用户是否获取了秒杀地址
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        // 校验前端的path和后端的path是否相同
        return path.equals(redisPath);
    }

    /**
     * 校验验证码
     *
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
//        if (StringUtils.isEmpty(captcha)) {
//            return false;
//        }
//        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + "," + goodsId);
//        return captcha.equals(redisCaptcha);
        return true;    //为了方便
    }
}
