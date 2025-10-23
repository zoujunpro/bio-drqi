package com.bio.drqi.manage.sample.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SampleApplyListPageRspDTO {
    private Integer id;

    /**
     * 取样申请编号
     */
    private String applyNo;

    /**
     * 取样申请数量
     */
    private Integer applyNumber;

    /**
     * 取样申请时间
     */
    private Date applyTime;

    /**
     * 取样申请人ID
     */
    private Integer applyUserId;

    /**
     * 取样申请人
     */
    private String applyUserName;

    /**
     * 取样工单描述
     */
    private String applyDesc;

    /**
     * 取样类型 F首次取样   R重复取样
     */
    private String applyType;

    /**
     * 鉴定引物地址
     */
    private String identifyExcelUrl;

    /**
     * 一代测序文件地址
     */
    private String oneTestExcelUrl;

    /**
     * NGS测序文件地址
     */
    private String ngsExcelUrl;


    /**
     * 是否是克隆苗取样 Y N
     */
    private String cloneFlag;

    /**
     * 孔板类型 one,more
     */
    private String layoutFlag;

    /**
     * 实施方案编号
     */
    private String vectorTaskCodes;

    /**
     * 取样编号范围
     */
    private String sampleCodeRange;

}
