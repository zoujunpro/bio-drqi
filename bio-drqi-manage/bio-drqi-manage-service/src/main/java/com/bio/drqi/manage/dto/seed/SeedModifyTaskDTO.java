package com.bio.drqi.manage.dto.seed;

import lombok.Data;

import java.util.Map;

@Data
public class SeedModifyTaskDTO {

    private String seedNum;

    private Map<String, ModifyValueContent> modifyContentMap;


    @Data
    public static class ModifyValueContent {
        private String oldFieldValue;
        private String newFieldValue;
    }
}
