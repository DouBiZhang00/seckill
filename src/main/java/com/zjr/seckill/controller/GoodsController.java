package com.zjr.seckill.controller;

import com.zjr.seckill.entity.User;
import com.zjr.seckill.entity.vo.DetailsVo;
import com.zjr.seckill.entity.vo.GoodsVo;
import com.zjr.seckill.service.IGoodsService;
import com.zjr.seckill.vo.RespBean;
import com.zjr.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    public static final String GOODSLIST_VIEW = "goodsList";
    public static final String GOODSDETAIL_VIEW = "goodsDetail";

    /**
     * 跳转商品列表页
     * 从redis读取缓存，若redis中有页面，直接返回页面给浏览器。若无页面，则渲染
     * @param model 页面需要展示的信息
     * @param user 自定义用户参数，传入前已判断其是否为空
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user,
                         HttpServletRequest request, HttpServletResponse response){
        WebContext context = null;
        if (user == null) {
            model.addAttribute("error", RespBeanEnum.NOT_LOGIN_ERROR);
            context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
            log.info("登录信息失效");
            return thymeleafViewResolver.getTemplateEngine().process("login", context);
        }
        log.info("由redis获取的user: {}", user);
        // 获取redis中的页面缓存
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get(GOODSLIST_VIEW);
        // 如果页面存在，返回缓存中的页面
        if (!StringUtils.isEmpty(html)) {
            log.info("页面:{} 可以直接从redis返回", GOODSLIST_VIEW);
            return html;
        }
        // 否则手动渲染页面
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.selectGoodsVo());
        context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
        if (!StringUtils.isEmpty(html)) {
            // 失效时间60s
            valueOperations.set(GOODSLIST_VIEW, html, 60, TimeUnit.SECONDS);
        }
        // goodsList.html
        return html;
    }

    /**
     * 跳转商品详情页
     * @return
     */
    @RequestMapping(value = "/toDetail/{goodsId}")
    @ResponseBody
    public ResponseEntity<RespBean> toDetail(User user, @PathVariable Long goodsId) {
        if (user == null) {
            log.info("登录信息失效");
            return ResponseEntity.ok(RespBean.error(RespBeanEnum.NOT_LOGIN_ERROR));
        }
        GoodsVo goodsVo = goodsService.selectGoodsVoById(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态
        int seckillStatus;
        // 秒杀倒计时
        int startingTime = 0, endingTime = 0;
        if (nowDate.before(startDate)) {
            // 秒杀未开始, 秒杀倒计时(开始时间 - 现在时间)
            seckillStatus = 0;
            startingTime = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
            endingTime = (int) ((endDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            // 秒杀已结束
            seckillStatus = 2;
        } else {
            // 秒杀进行中, 结束倒计时(结束时间 - 现在时间)
            seckillStatus = 1;
            startingTime = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
            endingTime = (int) ((endDate.getTime() - nowDate.getTime()) / 1000);
        }
        DetailsVo detailsVo = new DetailsVo();
        detailsVo.setUser(user);
        detailsVo.setStartingTime(startingTime);
        detailsVo.setEndingTime(endingTime);
        detailsVo.setSeckillStatus(seckillStatus);
        detailsVo.setGoods(goodsVo);
        log.info(detailsVo.toString());
        return ResponseEntity.ok(RespBean.success(detailsVo));
    }
}
