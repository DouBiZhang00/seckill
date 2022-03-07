package com.zjr.seckill.entity.vo;

import com.zjr.seckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 详情页返回对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsVo {

    private User user;

    private GoodsVo goods;

    private int startingTime;

    private int endingTime;

    private int seckillStatus;
}
