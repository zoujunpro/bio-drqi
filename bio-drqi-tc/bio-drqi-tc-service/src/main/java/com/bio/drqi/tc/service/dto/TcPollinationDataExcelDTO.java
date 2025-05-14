package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TcPollinationDataExcelDTO {
    /**
     * 实验编号
     */
    @ExcelProperty("试验编号(母本)")
    private String mExperimentNum;


    /**
     * 母本小区编号
     */
    @ExcelProperty("小区编号(母本)")
    private String mRegionNum;
    /**
     * 母本种子编号
     */
    @ExcelProperty("种子编号(母本)")
    private String mSeedNum;
    /**
     * 母本单株编号
     */
    @ExcelProperty("单株编号(母本)")
    private String mSampleCode;
    /**
     * 母本品种
     */
    @ExcelProperty("品种(母本)")
    private String mBreedName;
    /**
     * 母本实施方案编号
     */
    @ExcelProperty("实施方案编号(母本)")
    private String mVectorTaskCode;
    /**
     * 母本世代
     */
    @ExcelProperty("世代(母本)")
    private String mGenerationCode;
    /**
     * 母本基因类型
     */
    @ExcelProperty("基因型(母本)")
    private String mTcGene;


    @ExcelProperty("试验编号(父本)")
    private String fExperimentNum;
    /**
     * 父本小区编号
     */
    @ExcelProperty("小区编号(父本)")
    private String fRegionNum;
    /**
     * 父本种子编号
     */
    @ExcelProperty("种子编号(父本)")
    private String fSeedNum;

    /**
     * 父本单株编号
     */
    @ExcelProperty("单株编号(父本)")
    private String fSampleCode;
    /**
     * 父本品种
     */
    @ExcelProperty("品种(父本)")
    private String fBreedName;

    /**
     * 父本实施方案编号
     */
    @ExcelProperty("实施方案编号(父本)")
    private String fVectorTaskCode;
    /**
     * 父本世代
     */
    @ExcelProperty("世代(父本)")
    private String fGenerationCode;
    /**
     * 父本基因类型
     */
    @ExcelProperty("基因型(父本)")
    private String fTcGene;

    /**
     * 授粉时间
     */
    @ExcelProperty("授粉时间")
    private String pollinationDate;

    /**
     * 授粉方式名称
     */
    @ExcelProperty("授粉方式")
    private String pollinationMethodName;
    /**
     * 收获方式名称
     */
    @ExcelProperty("收获方式")
    private String harvestTypeName;
    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;
}
