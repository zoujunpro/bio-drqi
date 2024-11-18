package com.bio.drqi.domain;

import java.util.Date;


public class BaseDoMain {

    private Date createTime;

    private Date updateTime;

    public Date getCreateTime() {
        return new Date();
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        new Date();
    }
}
