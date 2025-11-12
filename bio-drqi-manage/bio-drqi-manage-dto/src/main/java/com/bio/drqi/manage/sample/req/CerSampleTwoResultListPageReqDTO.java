package com.bio.drqi.manage.sample.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class CerSampleTwoResultListPageReqDTO extends PageDTO {
    /**
     * 申请编号
     */
    private String applyNo;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 材料名称
     */
    private String sampleId;

    /**
     * 测序编号
     */
    private String runId;

    /**
     * 上传编号
     */
    private String uploadNum;

    /**
     * 检测渠道 1 项目 2大田
     */
    private String testChannel;

    private String synResult;





}
