package com.pinyougou.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by a2363196581 on 2018/3/6.
 */
public class PageResult implements Serializable{
    private long total;
    private List rows;

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
