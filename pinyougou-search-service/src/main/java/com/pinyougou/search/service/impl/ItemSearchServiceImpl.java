package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a2363196581 on 2018/3/17.
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map map = new HashMap();
        Query query = new SimpleQuery();

        map.putAll(searchItemList(searchMap));

        map.put("categoryList", searchCategoryList(searchMap));

        if (!"".equals(searchMap.get("category"))) {
            map.putAll(searchBrandAndSpecList((String) searchMap.get("category")));
        } else {
            if (searchCategoryList(searchMap).size() > 0) {
                map.putAll(searchBrandAndSpecList(searchCategoryList(searchMap).get(0)));
            }
        }
        return map;

    }


    public List<String> searchBrandList(Map searchMap) {
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_brand");
        query.setGroupOptions(groupOptions);

        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_brand");
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            list.add(tbItemGroupEntry.getGroupValue());
        }
        return list;
    }


    public Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);


        if (typeId != null) {
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);
            List spaeList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", spaeList);
        }
        return map;
    }


    public List<String> searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            list.add(tbItemGroupEntry.getGroupValue());
        }
        return list;
    }


    /**
     * 查询商品列表
     *
     * @param searchMap
     * @return
     */
    public Map searchItemList(Map searchMap) {
        Map map = new HashMap();
        HighlightQuery highlightQuery = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        highlightQuery.setHighlightOptions(highlightOptions);


        Criteria item_keywords = new Criteria("item_keywords").is(searchMap.get("keywords"));
        highlightQuery.addCriteria(item_keywords);

        /**
         * 过滤分类
         */
        if (!"".equals(searchMap.get("category"))) {
            Criteria criteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(criteria);
            highlightQuery.addFilterQuery(filterQuery);
        }


        /**
         * 过滤品牌
         */
        if (!"".equals(searchMap.get("brand"))) {
            Criteria criteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(criteria);
            highlightQuery.addFilterQuery(filterQuery);
        }


        /**
         * 过滤价格
         */
        if (!"".equals(searchMap.get("price"))) {
            String[] prices = ((String) searchMap.get("price")).split("-");
            Criteria criteria1 = new Criteria("item_price").greaterThanEqual(prices[0]);
            Criteria criteria2 = new Criteria("item_price").lessThanEqual(prices[1]);
            FilterQuery filterQuery1 = new SimpleFilterQuery(criteria1);
            FilterQuery filterQuery2 = new SimpleFilterQuery(criteria2);
            highlightQuery.addFilterQuery(filterQuery1);
            highlightQuery.addFilterQuery(filterQuery2);
        }


        /**
         * 过滤规格
         */
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map) searchMap.get("spec");

            for (String s : specMap.keySet()) {
                Criteria criteria = new Criteria("item_spec_" + s).is(specMap.get(s));
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }


        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 20;
        }
        highlightQuery.setOffset(pageNo);
        highlightQuery.setRows(pageSize);


        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
        List<HighlightEntry<TbItem>> entryList = tbItems.getHighlighted();
        for (HighlightEntry<TbItem> tHighlightEntry : entryList) {
            TbItem item = tHighlightEntry.getEntity();//未处理的商品
            List<HighlightEntry.Highlight> highlights = tHighlightEntry.getHighlights();//显示高亮的集合
            String title=null;
            try {
                 title = highlights.get(0).getSnipplets().get(0);
            }catch (Exception e){
                map.put("totalPages", "");
                map.put("total","");
                map.put("rows", "");
                return map;
            }

            item.setTitle(title);

        }

        List<TbItem> content = tbItems.getContent();
        map.put("totalPages", tbItems.getTotalPages());
        map.put("total", tbItems.getTotalElements());
        map.put("rows", content);
        return map;
    }
}
