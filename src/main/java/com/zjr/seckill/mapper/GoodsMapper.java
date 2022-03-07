package com.zjr.seckill.mapper;

import com.zjr.seckill.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjr.seckill.entity.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zjr
 * @since 2021-12-20
 */

public interface GoodsMapper extends BaseMapper<Goods> {
    /**
     * 查询商品列表，包括商品信息及其秒杀商品信息
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
