package com.bio.drqi.timePlan;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class VectorTaskTimePlanExportDTO {

    @ExcelProperty("项目编号")
    private String projectCode;

    @ExcelProperty("项目名称")
    private String projectName;

    @ExcelProperty("子项目编号")
    private String subProjectCode;

    @ExcelProperty("子项目名称")
    private String subProjectName;

    @ExcelProperty("实时方案编号")
    private String vectorTaskCode;

    @ExcelProperty("实时方案名称")
    private String vectorTaskName;

    @ExcelProperty("负责人")
    private String userName;
    /**
     * 事件类型
     */
    @ExcelProperty("事件类型")
    private String eventTypeName;

    /**
     * 预估开始时间
     */
    @ExcelProperty("预估开始时间")
    private String estimatedStartTime;

    /**
     * 预估结束时间
     */
    @ExcelProperty("预估结束时间")
    private String estimatedEndTime;

    /**
     * 实际开始时间
     */
    @ExcelProperty("实际开始时间")
    private String actualStartTime;

    /**
     * 实际结束时间
     */
    @ExcelProperty("实际结束时间")
    private String actualEndTime;


}
