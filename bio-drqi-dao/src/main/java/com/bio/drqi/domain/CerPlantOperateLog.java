package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 苗报备记录表
 * @TableName cer_plant_report_log
 */
@Data
public class CerPlantOperateLog implements Serializable {
    /**
     * 
     */
    @TableId( type = IdType.AUTO)
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


    private String operateCode;

    private String operateName;

    private static final long serialVersionUID = 1L;


}