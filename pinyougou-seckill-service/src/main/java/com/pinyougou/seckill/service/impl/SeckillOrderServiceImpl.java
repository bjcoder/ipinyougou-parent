package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by a2363196581 on 2018/4/3.
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;
    @Override
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

        return seckillOrder;
    }

    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (seckillOrder==null){
            throw new RuntimeException("订单不存在");
        }
        if(seckillOrder.getId().longValue()!=orderId){
            throw new RuntimeException("订单编号不一致");
        }

        seckillOrder.setTransactionId(transactionId);
        seckillOrder.setStatus("1");
        seckillOrder.setPayTime(new Date());
        seckillOrderMapper.insert(seckillOrder);
        redisTemplate.boundHashOps("seckillOrder").delete(userId);
    }

    @Override
    public void deleteOrder(String userId) {
        //获取缓存中的订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        //清楚缓存中该用户的过时订单
        redisTemplate.boundHashOps("seckillOrder").delete(userId);
        //获取缓存中的商品
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
        //恢复库存
        if(seckillGoods!=null){
            seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
            redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
        }
    }


}
