package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a2363196581 on 2018/3/29.
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item==null){
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(item.getStatus())){
            throw new RuntimeException("商品状态异常");
        }
        String sellerId = item.getSellerId();
        Cart cart = searchCartBySellerId(cartList, sellerId);
        if (cart==null){
            cart=new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            List<TbOrderItem> orderItems=new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(item,num);
            orderItems.add(orderItem);
            cart.setOrderItemList(orderItems);
            cartList.add(cart);
        }else {
            TbOrderItem orderItem =searchOrderItemByItemId(cart.getOrderItemList(),item.getId());
            if (orderItem==null){
                orderItem=createOrderItem(item,num);
                cart.getOrderItemList().add(orderItem);
            }else {
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                if (orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                    if(cart.getOrderItemList().size()<=0){//判断购物车明细列表是否为空 移除该购物车对象
                        cartList.remove(cart);
                    }
            }
            }
        }

        return cartList;
    }


    /**
     * 将购物车信息存到redis
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);



    }

    /**
     * 从redis取出购物车
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList =(List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        return cartList;
    }

    /**
     * 如果cookie和redis都有值，合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        if (cartList2!=null){
            for (Cart cart : cartList2) {
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    cartList1=addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
                }
            }
        }

        return cartList1;
    }

    /**
     * 查询订单明细列表中是否存在该商品的明细对象
     * @param orderItemList
     * @param id SKUid
     * @return 明细对象
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 根据SKU对象和购买数量构建TbOrderItem
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num<0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);//购买数量
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));

        return orderItem;
    }

    /**
     * 通过sellerId查找cartList中是否存在该商家的购物车对象
     * @param cartList
     * @param sellerId
     * @return 购物车对象
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (sellerId.equals(cart.getSellerId())){
                return cart;
            }
        }
        return null;
    }



}
