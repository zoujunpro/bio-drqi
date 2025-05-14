package com.bio.drqi.tc.req;


import lombok.Data;

import java.util.List;

@Data
public class TcPollinationCreatePollinationExcelReqDTO {
    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 取样批次号
     */
    private String sampleApplyNum;


}
