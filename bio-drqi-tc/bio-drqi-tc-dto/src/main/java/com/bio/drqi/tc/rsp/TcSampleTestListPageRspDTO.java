package com.bio.drqi.tc.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TcSampleTestListPageRspDTO {
    private Integer id;

    /**
     * 申请编号
     */
    private String sampleApplyNum;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 取样组织
     */
    private String sampleOrganize;

    /**
     * 取样类型 F首次取样   R重复取样
     */
    private String applyType;

    /**
     * 取样类型 one 单管    more 96孔板
     */
    private String testType;

    /**
     * 预计取样时间
     */
    private String expectedSampleTime;

    /**
     * 预计检测结果返回时间
     */
    private String expectedResultTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名字
     */
    private String createUserName;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;


    /**
     * 测序类型 1 NGS测序  2一代测序
     */
    private String sequenceType;

    /**
     *测序结果文件
     */
    private String resultExcelUrl;

    /**
     * 孔板引物排版文件
     */
    private String identifyPrimerExcelUrl;

}
