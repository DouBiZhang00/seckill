package com.zjr.seckill.entity.vo;

import com.zjr.seckill.entity.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品返回对象, 把[商品]和[秒杀商品]结合起来
 * 继承Goods意味着Goods的字段也继承了过来
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo extends Goods {
    /**
     * 秒杀价
     */
    private BigDecimal seckillPrice;

    /**
     * 库存数量
     */
    private Integer stockCount;

    /**
     * 秒杀开始时间
     */
    private Date startDate;

    /**
     * 秒杀结束时间
     */
    private Date endDate;
}
