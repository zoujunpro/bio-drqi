package com.bio.drqi.manage.sample.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class CerSampleOneResultListPageReqDTO extends PageDTO {

    /**
     * 取样编号
     */
    private String sampleCode;


    /**
     * 检测渠道     project,field
     */
    private String testChannel;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 上传编号
     */
    private String uploadNum;

    private String noMatchFlag;

}
