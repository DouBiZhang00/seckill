package com.zjr.seckill.service;

import com.zjr.seckill.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjr.seckill.entity.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjr
 * @since 2021-12-20
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 获取商品列表，包括商品和其对应的秒杀商品
     * @return
     */
    List<GoodsVo> selectGoodsVo();

    /**
     * 获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVo selectGoodsVoById(Long goodsId);
}
