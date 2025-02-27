package com.bio.drqi.seed;

import com.bio.drqi.BigDecimalSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class QuerySeedDestructionTaskRspDTO {

    private String taskNum;

    private String taskDesc;

    private List<SeedDestructionContent> contentList=new ArrayList<>();



    @Data
    public static class SeedDestructionContent {
        private String seedNum;

        @JsonSerialize(using = BigDecimalSerialize.class)
        private BigDecimal seedNumber;
        private String unit;

        public SeedDestructionContent(String seedNum, BigDecimal seedNumber, String unit) {
            this.seedNum = seedNum;
            this.seedNumber = seedNumber;
            this.unit = unit;
        }

        public SeedDestructionContent() {
        }
    }
}
