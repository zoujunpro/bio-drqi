package com.bio.drqi.manage.dto.seed;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SeedDestructionDTO extends BaseBioTaskDTO {


    private String destructionLocation;

    private String destructionMethod;

    private List<String> destructionEvidenceList;


    private List<SeedDTO> seedList;


    @Data
    public static class SeedDTO{

        private String seedNum;
        /**
         * 种子数量
         */
        @NotNull(message = "缺少种子销毁数量")
        private BigDecimal seedNumber;


        private String remarks;

        private String unit;


    }
}
