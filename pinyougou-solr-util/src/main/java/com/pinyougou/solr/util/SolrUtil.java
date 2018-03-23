package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by a2363196581 on 2018/3/17.
 */
@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    public void importItem(){
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(example);
            for (TbItem item:tbItems){
                Map specMap = JSON.parseObject(item.getSpec());
                item.setSpecMap(specMap);
                System.out.println(item.getId());
            }
            solrTemplate.saveBeans(tbItems);
            solrTemplate.commit();
    }

    public static void main(String[] strings){
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil)applicationContext.getBean("solrUtil");
        solrUtil.importItem();
    }
}
