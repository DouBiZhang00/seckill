package com.zjr.seckill.service.impl;

import com.zjr.seckill.entity.Goods;
import com.zjr.seckill.entity.vo.GoodsVo;
import com.zjr.seckill.mapper.GoodsMapper;
import com.zjr.seckill.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zjr
 * @since 2021-12-20
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> selectGoodsVo() {
        return goodsMapper.selectGoodsVo();
    }

    @Override
    public GoodsVo selectGoodsVoById(Long goodsId) {
        return goodsMapper.selectGoodsVoById(goodsId);
    }
}
