package com.zjr.seckill.controller;


import com.zjr.seckill.entity.User;
import com.zjr.seckill.entity.vo.OrderDetailVo;
import com.zjr.seckill.service.IOrderService;
import com.zjr.seckill.vo.RespBean;
import com.zjr.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zjr
 * @since 2021-12-20
 */
@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public ResponseEntity<RespBean> getDetail(User user, Long orderId) {
        if (user == null) {
            log.warn("登录信息失效");
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.NOT_LOGIN_ERROR));
        }
        if (orderId == null) {
            log.warn("订单不存在");
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.NOT_ORDER_ERROR));
        }
        OrderDetailVo orderVo = orderService.selectOrderDetail(orderId);
        orderVo.setUser(user);
        return ResponseEntity.ok(RespBean.success(orderVo));
    }

}
