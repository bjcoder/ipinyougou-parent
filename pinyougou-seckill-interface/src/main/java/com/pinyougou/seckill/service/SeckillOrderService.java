package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;

import java.util.Map;

/**
 * Created by a2363196581 on 2018/4/3.
 */
public interface SeckillOrderService {


    /**
     * 根据用户名查询秒杀订单
     * @param userId
     */
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId);


    /**
     * 支付成功保存订单
     * @param userId
     * @param orderId
     */
    public void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);


    public void deleteOrder(String userId);
}
