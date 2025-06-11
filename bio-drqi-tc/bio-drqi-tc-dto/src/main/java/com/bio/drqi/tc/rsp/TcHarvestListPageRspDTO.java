package com.bio.drqi.tc.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class TcHarvestListPageRspDTO {
    private Integer id;

    /**
     * 任务编号
     */
    private String taskNum;


    /**
     * 收获批次号
     */
    private String harvestApplyNum;

    /**
     * 收获时间
     */
    private String harvestTime;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 收获文件
     */
    private String harvestFileUrl;
}
