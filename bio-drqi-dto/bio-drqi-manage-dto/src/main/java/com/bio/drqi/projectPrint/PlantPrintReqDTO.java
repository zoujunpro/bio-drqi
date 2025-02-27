package com.bio.drqi.projectPrint;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlantPrintReqDTO {

    private List<Content> contentList=new ArrayList<>();

    @Data
    public static class Content {

        private String vectorTaskCode;

        private String plantCode;

        private Integer printNum;
    }

    public static void main(String[] args) {
        PlantPrintReqDTO plantPrintReqDTO =new PlantPrintReqDTO();
        Content content=new Content();
        content.setVectorTaskCode("xx01101-05");
        content.setPlantCode("AG10-01");
        plantPrintReqDTO.getContentList().add(content);
        System.out.println(JSONUtil.toJsonStr(plantPrintReqDTO));


    }
}
