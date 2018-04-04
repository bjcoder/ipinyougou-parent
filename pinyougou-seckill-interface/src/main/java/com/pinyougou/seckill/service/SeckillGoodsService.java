package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;

import java.util.List;

/**
 * Created by a2363196581 on 2018/4/3.
 */
public interface SeckillGoodsService {
    /**
     * 返回当前正在参与秒杀的商品
     * @return
     */
    public List<TbSeckillGoods> findList();


    /**
     * 根据ID获取实体(从缓存中读取)
     */
    public TbSeckillGoods findOneFromRedis(Long id);


    /**
     * 提交订单
     * @param seckillId
     * @param userId
     */
    public void submitOrder(Long seckillId,String userId);
}
