package com.bio.drqi.projectPrint;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TransPrintReqDTO {

    private String taskNum;

    private List<Content> contentList=new ArrayList<>();

    @Data
    public static class Content {

        private String vectorTaskCode;

        private String sampleCode;

        private String transformCode;

        private Integer printNum;
    }

}
