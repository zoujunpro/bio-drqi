package com.bio.drqi.manage.project.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class ConversionAndTransReqDTO extends PageDTO {


    /**
     * 任务编号
     */
    private String taskNum;


    private String transType;
}
