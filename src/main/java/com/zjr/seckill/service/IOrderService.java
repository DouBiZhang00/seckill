package com.zjr.seckill.service;

import com.zjr.seckill.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjr.seckill.entity.User;
import com.zjr.seckill.entity.vo.GoodsVo;
import com.zjr.seckill.entity.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjr
 * @since 2021-12-20
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀操作，生成秒杀订单
     * @param user
     * @param goodsVo
     * @return
     */
    Order seckill(User user, GoodsVo goodsVo);

    /**
     * 订单详情，根据订单id获取详情
     * @param orderId
     * @return
     */
    OrderDetailVo selectOrderDetail(Long orderId);

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);

    /**
     * 校验用户是否获取了秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    boolean checkPath(User user, Long goodsId, String path);

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
