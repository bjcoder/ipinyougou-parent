package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;


/**
 * Created by a2363196581 on 2018/3/25.
 */
@Component
public class ItemDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage message1 = (ObjectMessage) message;
        Long[] goodsIds = new Long[0];
        try {
            goodsIds = (Long[]) message1.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ItemDeleteListener监听接收到消息..."+goodsIds);

    }
}
