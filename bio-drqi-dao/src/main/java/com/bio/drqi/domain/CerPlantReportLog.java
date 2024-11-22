package com.bio.drqi.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 苗报备记录表
 * @TableName cer_plant_report_log
 */
@Data
public class CerPlantReportLog implements Serializable {
    /**
     * 
     */
    private Long id;

    /**
     * 种植编号
     */
    private String plantCode;

    /**
     * 图片
     */
    private String pictureUrls;

    /**
     * 描述
     */
    private String remark;

    /**
     * 报备时间
     */
    private Date createTime;

    /**
     * 报备人姓名
     */
    private Integer createUserId;

    /**
     * 报备人名称
     */
    private String createUserName;

    /**
     * 植株属性
     */
    private String plantAttribute;


    private static final long serialVersionUID = 1L;


}