package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by a2363196581 on 2018/4/3.
 */
@RestController
@RequestMapping("/seckillgoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    @RequestMapping("/findList")
    public List<TbSeckillGoods> findList(){
       return seckillGoodsService.findList();
    }

    @RequestMapping("/findOneFromRedis")
    public TbSeckillGoods findOneFromRedis(Long id) {

        return seckillGoodsService.findOneFromRedis(id);
    }


    @RequestMapping("/submitOrder")
    public Result submitOrder(Long seckillId){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if("anonymousUser".equals(userId)){//如果未登录
            return new Result(false, "用户未登录");
        }

        try {
            seckillGoodsService.submitOrder(seckillId,userId);
            return new Result(true, "下单成功");
        }catch (RuntimeException e){
            return new Result(false, e.getMessage());
        }catch (Exception e){
            return new Result(false, "下单失败");
        }


    }
}
