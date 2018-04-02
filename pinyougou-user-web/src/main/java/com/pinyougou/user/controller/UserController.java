package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.remoting.exchange.Request;
import com.pinyougou.common.CookieUtil;
import com.pinyougou.common.PhoneFormatCheckUtils;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by a2363196581 on 2018/3/26.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @Autowired
    private HttpServletRequest request;


    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/add")
    public Result add(@RequestBody TbUser user,String code){
        try {
            if (!PhoneFormatCheckUtils.isChinaPhoneLegal(user.getPhone())){
                return new Result(false,"手机格式错误");
            }
            if (!userService.checkCode(user.getPhone(),code)){
                return new Result(false,"验证码输入错误");
            }
            userService.add(user);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }

    }

    @RequestMapping("/createCode")
    public Result createCode(String mobile){
        try {

            userService.createSmsCode(mobile);
            return new Result(true, "增加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }


    @RequestMapping("/showName")
    public Map showName(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        CookieUtil.setCookie(request,response,"user",name,3600,"utf-8");

        Map<String,String> map=new HashMap();
        map.put("name",name);
        return map;
    }
}
