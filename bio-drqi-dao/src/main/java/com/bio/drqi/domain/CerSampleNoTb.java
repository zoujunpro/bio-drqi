package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Date：2023-08-29
 * @Description：取样信息表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value ="cer_sample_no_tb")
public class CerSampleNoTb  implements Serializable {

    /**
     * 主键iD
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 转化编号
     */
    private String transformCode;

    /**
     * 取样编号前缀
     */
    private String sampleCodePrefix;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;


    /**
     * 取样发起批次号
     */
    private String applyBatchNo;


    /**
     * 质粒名称
     */
    private String plasmidName;
    /**
     * 载体编号(子项目编号)
     */
    private String vectorCode;
    /**
     * 项目ID
     */
    private Integer projectId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}