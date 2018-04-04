package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.IdWorker;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * Created by a2363196581 on 2018/4/3.
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;
    @Override
    public List<TbSeckillGoods> findList() {
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        if (seckillGoods==null||seckillGoods.size()==0){
            TbSeckillGoodsExample example=new TbSeckillGoodsExample();
            TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andStockCountGreaterThan(0);
          seckillGoods = seckillGoodsMapper.selectByExample(example);
            System.out.println("将秒杀商品存入缓存");
            for (TbSeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId(),seckillGood);
           }

        }
        return seckillGoods;
    }

    @Override
    public TbSeckillGoods findOneFromRedis(Long id) {
        TbSeckillGoods seckillGoods =(TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
        return seckillGoods;
    }

    @Override
    public void submitOrder(Long seckillId, String userId) {

        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        if (seckillGoods==null){
            throw new RuntimeException("该商品不存在");
        }
        if (seckillGoods.getStockCount()<=0){
            throw new RuntimeException("该商品售尽");
        }
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        if (seckillGoods.getStockCount()!=0){
            redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods);
        }else {
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
        }

        TbSeckillOrder seckillOrder=new TbSeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setSeckillId(seckillId);
        seckillOrder.setStatus("0");
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setUserId(userId);

        System.out.println(seckillOrder.getUserId());
        System.out.println(seckillOrder.getMoney());
        redisTemplate.boundHashOps("seckillOrder").put(userId,seckillOrder);
    }
}
