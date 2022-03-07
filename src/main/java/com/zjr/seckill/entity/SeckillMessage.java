package com.zjr.seckill.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀消息(用于MQ)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {

    private User user;

    private Long goodId;
}
