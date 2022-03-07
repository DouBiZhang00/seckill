package com.zjr.seckill.service;

import com.zjr.seckill.entity.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjr.seckill.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjr
 * @since 2021-12-20
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId  [not null]: 成功  [-1]: 秒杀失败  [0]: 排队中
     */
    Long getResult(User user, Long goodsId);
}
