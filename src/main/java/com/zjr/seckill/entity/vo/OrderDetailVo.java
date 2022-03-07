package com.zjr.seckill.entity.vo;

import com.zjr.seckill.entity.Order;
import com.zjr.seckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {

    private User user;

    private Order order;

    private GoodsVo goodsVo;
}
