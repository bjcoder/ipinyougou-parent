package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * Created by a2363196581 on 2018/3/29.
 */
public interface CartService {
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    public void saveCartListToRedis(String username, List<Cart> cartList);

    public List<Cart> findCartListFromRedis(String username);

    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}
