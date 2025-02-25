package com.bio.drqi.manage.service.clean;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CleanSeedDataDTO {
    @ExcelProperty(value = "种子来源渠道")
    private String source;

    @ExcelProperty(value = "代次")
    private String generation;

    @ExcelProperty(value = "种植编号")
    private String  plantNum;

    @ExcelProperty(value = "项目编号")
    private String projectCode;

    @ExcelProperty(value = "任务编号")
    private String projectTaskNum;

    @ExcelProperty(value = "上代种子编号")
    private String  parentNum;

    @ExcelProperty(value = "作物")
    private String species;

    @ExcelProperty(value = "品种")
    private String breedName;

    @ExcelProperty(value = "基因型性状")
    private String geneticCharacter;


    @ExcelProperty(value = "种子类型")
    private String seedType;

    @ExcelProperty(value = "生产地点")
    private String productAddress;

    @ExcelProperty(value = "收获日期")
    private String harvestTime;

    @ExcelProperty(value = "母本信息")
    private String matherInfo;

    @ExcelProperty(value = "父本信息")
    private String fatherInfo;

    @ExcelProperty(value = "收获方式")
    private String harvestType;

    @ExcelProperty(value = "当前剩余数量")
    private String seedNumber;


    @ExcelProperty(value = "入库时总量")
    private String totalNumber;


    @ExcelProperty(value = "计量单位")
    private String unit;

    @ExcelProperty(value = "授粉方式")
    private String pollinationMethod;

    @ExcelProperty(value = "备注")
    private String remark;

    @ExcelProperty(value = "别名")
    private String aliasName;

    @ExcelProperty(value = "绑定基因类型")
    private String geneType;

    @ExcelProperty(value = "编辑材料")
    private String materialType;
}
