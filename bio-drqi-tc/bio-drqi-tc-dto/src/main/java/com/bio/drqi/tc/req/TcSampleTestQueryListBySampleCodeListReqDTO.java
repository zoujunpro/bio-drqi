package com.bio.drqi.tc.req;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class TcSampleTestQueryListBySampleCodeListReqDTO {


    @NotEmpty(message = "参数缺失：无取样编号")
    private List<Content> contentList;

    @Data
    @Valid
    public static class Content{

        @NotBlank(message = "参数缺失：sampleCode")
        private String sampleCode;

    }
}
