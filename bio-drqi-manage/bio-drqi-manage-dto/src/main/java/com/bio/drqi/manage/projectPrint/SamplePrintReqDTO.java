package com.bio.drqi.manage.projectPrint;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class SamplePrintReqDTO {

    /**
     * small large
     */
    @NotEmpty(message = "需指定打印标签尺寸大小")
    private String labelType;


    @NotEmpty(message = "打印数据缺失")
    private List<String> sampleCodeList;


}
