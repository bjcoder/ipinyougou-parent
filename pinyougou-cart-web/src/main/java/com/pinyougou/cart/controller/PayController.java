package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.IdWorker;
import com.pinyougou.entity.Result;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by a2363196581 on 2018/4/1.
 */

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;
    @RequestMapping("/createNative")
    private Map createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(name);
        return weixinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result=null;
        int count=0;
        while (true){

            System.out.println(count);

            Map map = weixinPayService.queryPayStatus(out_trade_no);
            if (map==null){
                result = new Result(false,"支付出错");
                break;
            }

            if ("SUCCESS".equals(map.get("trade_state"))){
                result = new Result(true,"支付成功");
                orderService.updateOrderStatus(out_trade_no,(String) map.get("transaction_id"));
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            if (count==60){
                result = new Result(false,"支付超时");
                break;
            }
        }
        return result;
    }
}
