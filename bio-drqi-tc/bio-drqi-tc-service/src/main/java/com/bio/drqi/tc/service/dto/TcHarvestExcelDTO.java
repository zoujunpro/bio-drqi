package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.drqi.domain.TcExperimentDesignTb;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcHarvestExcelDTO {

    /**
     * 母本小区编号
     */
    @ExcelProperty(value = {"母本","小区编号(母)"})
    @NotBlank(message = "参数缺失：小区编号(母)")
    private String motherRegionNum;
    /**
     * 母本种子编号
     */
    @ExcelProperty(value = {"母本","种子编号(母)"})
    @NotBlank(message = "参数缺失：种子编号(母)")
    private String motherSeedNum;
    /**
     * 母本单株编号
     */
    @ExcelProperty(value ={"母本","单株编号(母)"})
    @NotBlank(message = "参数缺失：单株编号(母)")
    private String motherSampleCode;
    /**
     * 母本品种
     */
    @ExcelProperty(value ={"母本","品种(母)"})
    @NotBlank(message = "参数缺失：品种(母)")
    private String motherBreedName;
    /**
     * 母本实施方案编号
     */
    @ExcelProperty(value ={"母本","实施方案编号(母)"})
    @NotBlank(message = "参数缺失：实施方案编号(母)")
    private String motherVectorTaskCode;
    /**
     * 母本世代
     */
    @ExcelProperty(value ={"母本","世代(母)"})
    @NotBlank(message = "参数缺失：世代(母)")
    private String motherGenerationName;
    /**
     * 母本基因类型
     */
    @ExcelProperty(value ={"母本","基因型(母)"})
    @NotBlank(message = "参数缺失：基因型(母)")
    private String motherTcGene;
    /**
     * 父本小区编号
     */
    @ExcelProperty(value ={"父本","小区编号(父)"})
    @NotBlank(message = "参数缺失：小区编号(父)")
    private String fatherRegionNum;
    /**
     * 父本种子编号
     */
    @ExcelProperty(value ={"父本","种子编号(父)"})
    @NotBlank(message = "参数缺失：种子编号(父)")
    private String fatherSeedNum;

    /**
     * 父本单株编号
     */
    @ExcelProperty(value ={"父本","单株编号(父)"})
    @NotBlank(message = "参数缺失：单株编号(父)")
    private String fatherSampleCode;
    /**
     * 父本品种
     */
    @ExcelProperty(value ={"父本","品种(父)"})
    @NotBlank(message = "参数缺失：品种(父)")
    private String fatherBreedName;

    /**
     * 父本实施方案编号
     */
    @ExcelProperty(value ={"父本","实施方案编号(父)"})
    @NotBlank(message = "参数缺失：实施方案编号(父)")
    private String fatherVectorTaskCode;
    /**
     * 父本世代
     */
    @ExcelProperty(value ={"父本","世代(父)"})
    @NotBlank(message = "参数缺失：世代(父)")
    private String fatherGenerationName;
    /**
     * 父本基因类型
     */
    @ExcelProperty(value ={"基因型(父)"})
    @NotBlank(message = "参数缺失：基因型(父)")
    private String fatherTcGene;

    /**
     * 收获数量
     */
    @ExcelProperty(value ={"收获信息","收获数量"})
    @NotBlank(message = "参数缺失：收获数量")
    private String seedNumber;

    /**
     * 收获单位
     */
    @ExcelProperty(value ={"收获信息","收获单位"})
    @NotBlank(message = "参数缺失：收获单位")
    private String unit;

    /**
     * 收获方式名称
     */
    @ExcelProperty(value ={"收获信息","收获方式"})
    @NotBlank(message = "参数缺失：收获方式")
    private String harvestTypeName;


    /**
     * 备注
     */
    @ExcelProperty(value ={"收获信息","备注"})
    private String remark;





}
