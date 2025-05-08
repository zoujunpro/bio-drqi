package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BmsProjectListPageRspDTO {

    private Integer id;

    /**
     * 项目名称
     */
    private String projectCode;

    /**
     * 项目编号
     */
    private String projectName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;
}
