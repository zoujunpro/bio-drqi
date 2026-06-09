package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SurvivalCompetitionExperimentDesignExcelDTO extends ExperimentDesignExcelDTO {

    /**
     * 期次
     */
    @ExcelProperty("期次")
    private String period;
}
