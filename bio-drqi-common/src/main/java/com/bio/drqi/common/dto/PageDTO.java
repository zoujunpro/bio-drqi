package com.bio.drqi.common.dto;


public class PageDTO {

    /**主键ID*/
    private Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
