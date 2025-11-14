package com.bio.drqi.plant.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ExperimentExcelDTO {

    @ExcelProperty("区域")
    private String regionNum;

    @ExcelProperty("实施方案编号")
    private String vectorTaskCode;

    @ExcelProperty("种子编号")
    @NotBlank(message = "种子编号缺失")
    private String seedNum;

    @ExcelProperty("数量")
    @NotNull(message = "试验种子数量缺失")
    private Integer plantNumber;

    @ExcelProperty("种植时间")
    @NotNull(message = "种植时间缺失")
    private String plantTime;

    @ExcelProperty("试验地点")
    @NotNull(message = "试验地点缺失")
    private String experimentAddressName;

    @ExcelProperty("备注")
    private String remark;



}
