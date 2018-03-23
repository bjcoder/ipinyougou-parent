package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a2363196581 on 2018/3/21.
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;


    @Override
    public boolean genItemHtml(Long goodsId) {
        Configuration configuration=freeMarkerConfig.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");
            Map map=new HashMap();

            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            map.put("goods",tbGoods);
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //没有扩展属性
            if ("".equals(tbGoodsDesc.getCustomAttributeItems())||tbGoodsDesc.getCustomAttributeItems()==null){
                tbGoodsDesc.setCustomAttributeItems("[]");
            }
            //没有规格
            if ("".equals(tbGoodsDesc.getSpecificationItems())||tbGoodsDesc.getCustomAttributeItems()==null){
                tbGoodsDesc.setSpecificationItems("[]");
            }

            map.put("goodsDesc",tbGoodsDesc);

            TbItemExample itemExample=new TbItemExample();
            TbItemExample.Criteria criteria = itemExample.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            itemExample.setOrderByClause("is_default DESC");
            List<TbItem> tbItems = itemMapper.selectByExample(itemExample);


            map.put("itemList",tbItems);

            String category1Id = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String category2Id = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String category3Id = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            map.put("category1Id",category1Id);
            map.put("category2Id",category2Id);
            map.put("category3Id",category3Id);
            //创建流
            FileWriter fileWriter = new FileWriter(pagedir + goodsId + ".html");
            template.process(map,fileWriter);
            fileWriter.close();
        }catch (Exception e){

        }

        return false;
    }
}
