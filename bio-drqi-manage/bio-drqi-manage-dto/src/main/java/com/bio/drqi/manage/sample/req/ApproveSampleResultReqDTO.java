package com.bio.drqi.manage.sample.req;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ApproveSampleResultReqDTO {

    @NotBlank(message = "参数缺失：taskNum")
    private String taskNum;

    @Valid
    private List<Content> contentList;

    @Data
    public static class Content{

        @NotBlank(message = "参数缺失：sampleCode")
        private String sampleCode;

        @NotBlank(message = "参数缺失：vectorTaskCode")
        private String vectorTaskCode;

        @NotBlank(message = "参数缺失：checkResult")
        private String checkResult;
    }
}
