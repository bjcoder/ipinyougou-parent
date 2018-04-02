package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by a2363196581 on 2018/3/17.
 */
@RestController
@RequestMapping("/search")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;

    //搜索页面搜索
    @RequestMapping("/search")
    public Map search(@RequestBody Map searchMap){

        Map<String, Object> search = itemSearchService.search(searchMap);

        return search;
    }
}
