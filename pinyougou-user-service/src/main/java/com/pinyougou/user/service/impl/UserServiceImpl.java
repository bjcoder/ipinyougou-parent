package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by a2363196581 on 2018/3/26.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;
    
    @Autowired
    private Destination smsDestination;

    @Override
    public void add(TbUser user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        user.setStatus("Y");


        userMapper.insert(user);
    }

    @Override
    public void createSmsCode(final String phone) {
        final String smsCode =String.valueOf((int)((Math.random()*9+1)*100000));
        System.out.println("smsCode="+smsCode);
        redisTemplate.boundHashOps("smsCode").put(phone,smsCode);
        System.out.println("将验证码存入redis中");
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile", phone);
                mapMessage.setString("template_code", "SMS_128641067");
                mapMessage.setString("sign_name", "品优购商城");
                //{\"code\":\"343523\"} [] map
                Map map = new HashMap();
                map.put("code", smsCode);
                mapMessage.setString("param", JSON.toJSONString(map));


                return mapMessage;
            }
        });
    }

    @Override
    public boolean checkCode(String mobile, String code) {
        String smsCode = (String) redisTemplate.boundHashOps("smsCode").get(mobile);
        if (code==null||"".equals(code)){
            return false;
        }
        if (!code.equals(smsCode))
        return false;
        return true;
    }
}
