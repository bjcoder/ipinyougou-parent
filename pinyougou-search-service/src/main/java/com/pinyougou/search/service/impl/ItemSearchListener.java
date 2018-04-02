package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 * Created by a2363196581 on 2018/3/25.
 */
@Component
public class ItemSearchListener implements MessageListener{

    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到消息...");

        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();

            List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);

            for (TbItem tbItem : tbItems) {

                Map map=JSON.parseObject(tbItem.getSpec(),Map.class);
                tbItem.setSpecMap(map);

            }

            itemSearchService.importData(tbItems);

            System.out.println("成功导入到索引库");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
