package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.CookieUtil;
import com.pinyougou.entity.Result;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a2363196581 on 2018/3/29.
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;



    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartList = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (cartList==null||"".equals(cartList)){
            cartList="[]";
        }
        List<Cart> carts = JSON.parseArray(cartList, Cart.class);
        if("anonymousUser".equals(name)){//没有登陆 从cookie中获取
            System.out.println("从cookie中获取购物车对象");
            return carts;
        }else {//登陆了 从redis中获取
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
            if (carts.size()>0){
                cartListFromRedis = cartService.mergeCartList(carts,cartListFromRedis);
                CookieUtil.deleteCookie(request,response,"cartList");
                cartService.saveCartListToRedis(name,cartListFromRedis);
            }
            System.out.println("从redis中获取购物车对象");
            return cartListFromRedis;
        }

    }

    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num){
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9106");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            List<Cart> carList=findCartList();
            if (carList==null){
                carList=new ArrayList<>();
            }
            carList = cartService.addGoodsToCartList(carList, itemId, num);
            if("anonymousUser".equals(name)){
                String carListSrtring = JSON.toJSONString(carList);
                CookieUtil.setCookie(request,response,"cartList",carListSrtring,3600*24,"utf-8");
                System.out.println("从cookie中添加购物车对象");
            }else {
                cartService.saveCartListToRedis(name,carList);
                System.out.println("从redis中添加购物车对象");
            }
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/showName")
    public String showName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
       return name;
    }
}
