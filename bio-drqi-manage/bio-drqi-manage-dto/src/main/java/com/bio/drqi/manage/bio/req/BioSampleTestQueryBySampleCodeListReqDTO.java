package com.bio.drqi.manage.bio.req;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BioSampleTestQueryBySampleCodeListReqDTO {

    @NotEmpty(message = "没有入参数据")
    private List<Content> contentList;


    @Data
    @Valid
    public static class Content {

        @NotBlank(message = "取样编号缺失")
        private String sampleCode;

        private String cloneSeedNum;
    }
}
