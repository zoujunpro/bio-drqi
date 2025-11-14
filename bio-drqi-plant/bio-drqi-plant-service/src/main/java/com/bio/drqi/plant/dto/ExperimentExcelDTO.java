package com.bio.drqi.plant.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExperimentExcelDTO {

    @ExcelProperty("区域")
    private String regionCode;

    @ExcelProperty("实施方案编号")
    private String vectorTaskCode;

    @ExcelProperty("种子编号")
    private String seedNum;

    @ExcelProperty("数量")
    private String plantNum;

    @ExcelProperty("种植地点")
    private String experimentAddressName;

    @ExcelProperty("备注")
    private String remark;



}
