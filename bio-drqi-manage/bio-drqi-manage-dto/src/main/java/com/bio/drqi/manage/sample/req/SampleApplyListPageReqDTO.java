package com.bio.drqi.manage.sample.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class SampleApplyListPageReqDTO extends PageDTO {

    /**
     * 取样申请编号
     */
    private String applyNo;



    /**
     * 取样申请人ID
     */
    private Integer applyUserId;


    /**
     * 取样类型 F首次取样   R重复取样
     */
    private String applyType;

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
    private String vectorTaskCode;



}
