package com.zjr.seckill.exception;

import com.zjr.seckill.vo.RespBean;
import com.zjr.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler
    public RespBean ExceptionHandler(Exception e){
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            return respBean;
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
