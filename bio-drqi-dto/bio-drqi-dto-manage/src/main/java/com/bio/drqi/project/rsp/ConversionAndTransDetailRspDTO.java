package com.bio.drqi.project.rsp;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ConversionAndTransDetailRspDTO {

    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 转化疫苗ID
     */
    private Integer conversionAndTransId;

    /**
     * 取样编号
     */
    private String sampleCode;


    private String editPureUnion;

    private String acceptorMaterial;

    private String vectorTaskCode;

    private String subProjectCode;

    private String transformCode;

    private Integer transNum;

    /**
     * 项目编号
     */
    private String projectCode;
    /**
     * 是否转基因 Y-是,N-否 O-N/A
     */
    private String transGeneFlag;

    private String plasmidName;
}
