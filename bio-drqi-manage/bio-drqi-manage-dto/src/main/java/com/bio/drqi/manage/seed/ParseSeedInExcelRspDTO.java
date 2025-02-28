package com.bio.drqi.manage.seed;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ParseSeedInExcelRspDTO {

    @ExcelProperty(value = "种子来源")
    private String source;

    @ExcelProperty(value = "代数")
    private String generation;

    @ExcelProperty(value = "种植编号")
    private String plantNum;

    @ExcelProperty(value = "项目编号")
    private String projectCode;


    @ExcelProperty(value = "取样编号")
    private String sampleCode;

    @ExcelProperty(value = "上代种子编号")
    private String parentNum;

    @ExcelProperty(value = "作物")
    private String specieName;

    @ExcelProperty(value = "品种")
    private String breedName;



    @ExcelProperty(value = "生产地点")
    private String productAddress;



    @ExcelProperty(value = "母本信息")
    private String matherInfo;

    @ExcelProperty(value = "父本信息")
    private String fartherInfo;


    @ExcelProperty(value = "数量")
    private BigDecimal seedNumber;

    @ExcelProperty(value = "计量单位")
    private String unit;


    @ExcelProperty(value = "基因型")
    private String geneType;


    @ExcelProperty(value = "基因型性状")
    private String geneticCharacter;


    @ExcelProperty(value = "收获日期")
    private String harvestTime;

    @ExcelProperty(value = "收获方式")
    private String harvestType;


    @ExcelProperty(value = "授粉方式")
    private String pollinationMethod;

    @ExcelProperty(value = "种子类型")
    private String seedType;


    @ExcelProperty(value = "生产地点")
    private String productionLocationName;

    @ExcelProperty(value = "备注")
    private String remarks;

    @ExcelProperty(value = "材料类型")
    private String materialType;

    @ExcelProperty(value = "别名")
    private String aliasName;

    private String breedCode;

    private String speciesCode;



    private String storeFlag;

    private String uniqueCode;


}
