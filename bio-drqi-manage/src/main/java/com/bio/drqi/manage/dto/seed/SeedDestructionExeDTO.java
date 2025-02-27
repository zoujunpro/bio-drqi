package com.bio.drqi.manage.dto.seed;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SeedDestructionExeDTO extends SeedProcDTO {

    private String taskNum;

    private List<SeedDestructionContent> contentList=new ArrayList<>();


    @Data
    public static class SeedDestructionContent {
        @NotBlank
        private String seedNum;
        /**
         * 种子数量
         */
        private BigDecimal seedNumber;

        private String unit;

        private String destructionLocation;

        private String destructionMethod;

        private List<String> destructionEvidenceList;
    }

}
