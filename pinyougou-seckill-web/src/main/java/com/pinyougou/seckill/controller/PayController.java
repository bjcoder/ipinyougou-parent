package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by a2363196581 on 2018/4/3.
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @Reference
    private WeixinPayService weixinPayService;


    @RequestMapping("/createNative")
    private Map createNative(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(name);
        Long money = (long)(seckillOrder.getMoney().doubleValue()*100);
        System.out.println(money);
        System.out.println(seckillOrder.getId());
        return weixinPayService.createNative(seckillOrder.getId()+"",money+"");
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result=null;
        int count=0;
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        while (true){

            System.out.println(count);

            Map map = weixinPayService.queryPayStatus(out_trade_no);
            if (map==null){
                result = new Result(false,"支付出错");
                break;
            }

            if ("SUCCESS".equals(map.get("trade_state"))){
                result = new Result(true,"支付成功");
                seckillOrderService.saveOrderFromRedisToDb(userId,Long.valueOf(out_trade_no), (String)map.get("transaction_id"));
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            if (count>=10){
                result = new Result(false,"支付超时");
                Map pay = weixinPayService.closePay(out_trade_no);
                if("FAIL".equals(pay.get("result_code"))){
                    if("ORDERPAID".equals(pay.get("err_code"))) {
                        result=new Result(true,"支付成功");
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), (String) map.get("transaction_id"));
                    }
                }
                if(!result.isSuccess()){
                    seckillOrderService.deleteOrder(userId);
                }
                break;
            }
        }
        return result;
    }
}
