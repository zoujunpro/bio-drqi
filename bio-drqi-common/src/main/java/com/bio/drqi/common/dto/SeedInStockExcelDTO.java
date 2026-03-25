package com.bio.drqi.common.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.ExcelSelected;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class SeedInStockExcelDTO {
    /**
     * 来源
     */
    @ExcelProperty("种子来源")
    @ExcelSelected(source = {"CER","大棚","大田","外单位","网购","玻璃温室"})
    private String source;

    /**
     * 代次
     */
    @ExcelProperty( "代次")
    private String generation;


    /**
     * 种植编号
     */
    @ExcelProperty( "种植编号")
    private String plantCode;


    /**
     * 实施方案编号
     */
    @ExcelProperty( "实施方案编号")
    private String vectorTaskCode;

    @ExcelProperty( "材料类型")
    @ExcelSelected(source = {"基因编辑材料","转基因材料","常规材料","分离野生型"})
    private String materialTypeName;

    /**
     * 试验方案编号
     */
    @ExcelProperty( "试验方案编号")
    private String experimentNum;

    /**
     * 父本小区编号
     */
    @ExcelProperty( "父本小区编号")
    private String fatherRegionNum;

    /**
     * 母本小区编号
     */
    @ExcelProperty( "母本小区编号")
    private String matherRegionNum;

    /**
     * 父本单株编号
     */
    @ExcelProperty( "父本单株编号")
    private String fatherSingleNum;

    /**
     * 母本单株编号
     */
    @ExcelProperty( "母本单株编号")
    private String matherSingleNum;

    /**
     * 生产地点（天津/海南/新乡）
     */
    @ExcelProperty( "生产地点")
    private String productionLocationName;
    /**
     * 母本信息
     */
    @ExcelProperty( "母本信息")
    private String matherInfo;

    /**
     * 父本信息
     */
    @ExcelProperty( "父本信息")
    private String fatherInfo;


    /**
     * 母本种子编号
     */
    @ExcelProperty( "母本种子编号")
    private String matherSeedNum;

    /**
     * 父本种子编号
     */
    @ExcelProperty( "父本种子编号")
    private String fatherSeedNum;

    @ExcelProperty( "作物")
    private String speciesName;

    @ExcelProperty( "品种")
    private String breedName;
    /**
     * 收获方式，单珠和混珠
     */
    @ExcelProperty( "收获方式")
    @ExcelSelected(source = {"单株单检","单株","株系行收","混收","单检混收"})
    private String harvestTypeName;

    @ExcelProperty( "收获时间")
    private String harvestTime;
    /**
     * 授粉方式
     */
    @ExcelProperty( "授粉方式")
    @ExcelSelected(source = {"自交","杂交","测交","姊妹交","回交","无性繁殖"})
    private String pollinationMethodName;

    @ExcelProperty( "数量")
    private BigDecimal seedNumber;
    /**
     * 计量单位g/kg/粒
     */
    @ExcelProperty( "计量单位")
    @ExcelSelected(source = {"g", "kg", "粒","ml"})
    private String unit;

    @ExcelProperty( "别名")
    private String aliasName;
    /**
     * 备注
     */
    @ExcelProperty( "备注")
    private String remarks;


}
