package com.bio.drqi.manage.dto.seed;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SeedPreparationDTO extends SeedProcDTO{

    @NotBlank(message = "参数缺失：seedNum")
    private String seedNum;
    /**
     * 种子数量
     */
    @NotNull(message = "参数缺失：seedNumber")
    private BigDecimal seedNumber;

    private String unit;

    private String remarks;
}
