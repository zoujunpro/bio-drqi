package com.bio.drqi.tc.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class TcPollinationListPageRspDTO {
    private Integer id;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 取样批次号
     */
    private String sampleApplyNum;

    /**
     * 授粉方式
     */
    private String pollinationType;

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 任务工单号
     */
    private String taskNum;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人
     */
    private String createUserName;

    /**
     * 创建日期
     */
    private Date createTime;

    private String pollinationExcelUrl;





}
