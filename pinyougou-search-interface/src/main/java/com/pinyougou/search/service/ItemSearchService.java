package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * Created by a2363196581 on 2018/3/17.
 */
public interface ItemSearchService {

    public Map<String,Object> search(Map searchMap);

    public void importData(List<TbItem> list);

    public void deleteByGoodsIds(List list);
}
