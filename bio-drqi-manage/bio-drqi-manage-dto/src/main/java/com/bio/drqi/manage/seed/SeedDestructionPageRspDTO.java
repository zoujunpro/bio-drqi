package com.bio.drqi.manage.seed;

import com.bio.drqi.manage.BigDecimalSerialize;
import com.bio.drqi.manage.base.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class SeedDestructionPageRspDTO extends PageDTO {

    private Integer id;

    /**
     * 销毁地点
     */
    private String destructionLocation;

    /**
     * 种子号
     */
    private String seedNum;

    /**
     * 销毁方法
     */
    private String destructionMethod;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数量
     */
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal seedNumber;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 申请任务编号
     */
    private String applyTaskNum;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 销毁证据
     */
    private List<String> destructionEvidenceList;


    /**
     * 执行状态1执行中 2已执行
     */
    private String status;

    /***
     * 申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date applyDate;

    /***
     * 销毁时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date destructionDate;

}
