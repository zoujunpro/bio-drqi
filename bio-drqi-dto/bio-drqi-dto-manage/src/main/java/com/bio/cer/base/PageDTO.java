package com.bio.cer.base;

import lombok.Data;


public class PageDTO {

    /**每页记录数*/
    private Integer pageSize;

    /**第几页*/
    private Integer pageNum;


    public Integer getPageSize() {
        return pageSize==null?20:pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum==null?0:pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
}
