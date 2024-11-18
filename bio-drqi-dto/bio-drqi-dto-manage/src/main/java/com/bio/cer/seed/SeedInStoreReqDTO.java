package com.bio.cer.seed;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
public class SeedInStoreReqDTO {

    @NotBlank(message = "参数缺失：taskNum")
    private String taskNum;

    @Valid
    private List<Content> contentList;


    @Data
    public static class Content{

        @NotBlank(message = "参数缺失：uniqueCode")
        private String uniqueCode;
        @NotBlank(message = "参数缺失：stockLocationNum")
        private String stockLocationNum;

        private BigDecimal seedNumber;
    }

    public static void main(String[] args) {
        SeedInStoreReqDTO seedInStoreReqDTO=new SeedInStoreReqDTO();
        seedInStoreReqDTO.setTaskNum("S0000313");

        Content content=new Content();
        content.setUniqueCode("a2083e873dd64c41835cffc7f9c2881a");
        content.setStockLocationNum("354");
        seedInStoreReqDTO.setContentList(Arrays.asList(content));
        System.out.println(JSONUtil.toJsonStr(seedInStoreReqDTO));
    }

}
