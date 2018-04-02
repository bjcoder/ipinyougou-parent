package com.pinyougou.page.listener;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * Created by a2363196581 on 2018/3/25.
 */
public class PageDeleteListener implements MessageListener{
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage message1 = (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) message1.getObject();
            System.out.println("ItemDeleteListener监听接收到消息..."+ids);
            for (Long id : ids) {
                boolean b = itemPageService.deleteItemHtml(id);
                System.out.println("网页删除结果："+b);
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
