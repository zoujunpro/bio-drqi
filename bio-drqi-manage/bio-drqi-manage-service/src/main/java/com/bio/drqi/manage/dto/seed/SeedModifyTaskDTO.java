package com.bio.drqi.manage.dto.seed;

import lombok.Data;

import java.util.List;

@Data
public class SeedModifyTaskDTO {

    private String seedNum;

    private String vectorTaskCode;

    private List<ModifyValueContent> modifyValueContentList;


    @Data
    public static class ModifyValueContent {
        private String key;
        private String oldFieldValue;
        private String newFieldValue;
    }
}
