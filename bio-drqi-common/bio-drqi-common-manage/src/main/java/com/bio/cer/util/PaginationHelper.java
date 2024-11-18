package com.bio.cer.util;

import lombok.Data;

import java.util.Collections;
import java.util.List;

public class PaginationHelper<T> {
    private int pageNum;
    private int pageSize;
    private List<T> list;
    private Integer totalNum;

    public PaginationHelper(List<T> list, int currentPage, int pageSize) {
        this.list = list;
        this.pageNum = currentPage;
        this.pageSize = pageSize;
    }

    public List<T> getCurrentPageData() {
        if (list == null) {
            return null;
        }
        int total = list.size();
        int fromIndex = (pageNum - 1) * pageSize;
        if (fromIndex >= total) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + pageSize, total);
        return list.subList(fromIndex, toIndex);
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getTotalNum() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }

    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }
}
