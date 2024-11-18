package com.bio.cer.sample.req;


import lombok.Data;

import java.util.List;

@Data
public class ApplySampleReqDTO {
    /**项目Id*/
    private Integer projectId;

    /**转化编号*/
    List<String> transformNoList;
}
