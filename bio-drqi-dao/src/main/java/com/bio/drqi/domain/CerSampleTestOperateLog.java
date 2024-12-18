package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 对取样检测结果的剔苗保苗操作记录表
 * @TableName cer_sample_test_operate_log
 */
@Data
@TableName(value ="cer_sample_test_operate_log")
public class CerSampleTestOperateLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 实时方案编号
     */
    private String vectorTaskCode;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 实时方案ID
     */
    private Integer vectorTaskId;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 操作  剔苗 保苗
     */
    private String operateCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 图片地址
     */
    private String pictureUrls;

    /**
     * 执行时间
     */
    private Date createTime;

    /**
     * 执行人ID
     */
    private Integer createUserId;

    /**
     * 执行人名称
     */
    private String createUserName;

    private String uniqueCode;




    private static final long serialVersionUID = 1L;

}