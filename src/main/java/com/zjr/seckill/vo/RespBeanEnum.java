package com.zjr.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    // 成功的返回
    SUCCESS(200, "success"),
    // 失败的返回
    ERROR(500, "服务端异常"),
    // 登录模块
    NOT_LOGIN_ERROR(500200, "登录信息失效，请重新登录"),
    LOGIN_ERROR(500210, "用户名或密码不正确"),
    USER_NOT_EXIST(500213, "用户不存在"),
    UPDATE_PASSWORD_ERROR(500214, "更新密码失败"),
    // 注册模块
    REGISTER_ERROR(500211, "用户名已存在，请更换用户名再注册!"),
    // 参数校验异常
    BIND_ERROR(500212, "参数校验异常"),
    // 秒杀模块5005xx
    EMPTY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "该商品每人限购一件"),
    SECKILL_FAIL(500502, "秒杀失败"),
    REQUEST_ILLEGAL(500503, "请求非法, 请重新尝试"),
    CAPTCHA_ERROR(500504, "验证码错误, 请重新输入"),
    ACCESS_LIMIT(500505, "访问过于频繁，请稍后再试"),
    // 订单模块
    NOT_ORDER_ERROR(500512, "订单不存在");
    private final Integer code;
    private final String message;
}
