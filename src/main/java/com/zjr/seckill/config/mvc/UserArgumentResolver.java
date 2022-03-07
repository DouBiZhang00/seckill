package com.zjr.seckill.config.mvc;

import com.zjr.seckill.entity.User;
import com.zjr.seckill.service.IUserService;
import com.zjr.seckill.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义用户参数
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private IUserService userService;

    /**
     * 判断参数是否为用户，只有该方法为True时，才能继续执行下面那个方法
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        // 拿到request 和 respond
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        // 进行cookie是否为空的判断
        if (StringUtils.isEmpty(ticket)) {
            return null;
        }
        return userService.getUserByRedis(ticket, request, response);
    }
}
