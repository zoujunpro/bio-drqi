package com.bio.drqi.manage.sample.req;

import lombok.Data;

@Data
public class SampleResultFileUploadFileReqDTO {

    /**
     * 文件地址
     */
    private String excelUrl;


    /**
     * 一代测序 1  NGS结果2
     */
    private String resultType;
}
