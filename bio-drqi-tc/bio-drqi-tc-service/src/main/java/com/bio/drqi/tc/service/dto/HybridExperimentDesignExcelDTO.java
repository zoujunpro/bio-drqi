package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.drqi.tc.service.excel.ExcelSelected;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
public class HybridExperimentDesignExcelDTO extends ExperimentDesignExcelDTO {

    /**
     * 亲本类型
     */
    @ExcelProperty("亲本类型(F/M)")
    @NotBlank(message = "亲本类型必填")
    @Pattern(regexp = "F|M", message = "亲本类型只能填写F或M")
    @ExcelSelected({"F", "M"})
    private String parentType;

    /**
     * 错期设计
     */
    @ExcelProperty("错期设计")
    private String staggeredDesign;
}
