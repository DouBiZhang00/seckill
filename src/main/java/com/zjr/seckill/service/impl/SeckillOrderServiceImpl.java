package com.zjr.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjr.seckill.entity.SeckillOrder;
import com.zjr.seckill.entity.User;
import com.zjr.seckill.mapper.SeckillOrderMapper;
import com.zjr.seckill.service.ISeckillOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId  [not null]: 成功  [-1]: 秒杀失败  [0]: 排队中
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        log.info("尝试获取[用户{}] 所购买的[商品id为{}] 的商品详情", user.getUsername(), goodsId);
        // 根据userId和goodsId获取秒杀订单
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(
                new QueryWrapper<SeckillOrder>()
                        .eq("user_id", user.getId())
                        .eq("goods_id", goodsId)
        );
        // 订单不为空，返回订单号
        if (seckillOrder != null) {
            return seckillOrder.getOrderId();
        // 库存为空，返回-1
        } else if (redisTemplate.boundSetOps("isStockEmpty").isMember(goodsId)) {
            log.warn("通过redis的set集合发现商品 {} 的库存为空", goodsId);
            return -1L;
        } else {
            return 0L;
        }
    }
}
