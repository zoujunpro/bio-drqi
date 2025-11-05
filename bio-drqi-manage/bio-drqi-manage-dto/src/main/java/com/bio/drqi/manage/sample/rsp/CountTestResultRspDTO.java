package com.bio.drqi.manage.sample.rsp;

import lombok.Data;

@Data
public class CountTestResultRspDTO {

    /**
     * 所有检测检测结果数量
     */
    private Integer checkResultNum;


    /**
     * 二代检测结果数量
     */
    private Integer twoResultNum;

    /**
     * 无检查结果数量
     */
    private Integer notResultNum;
}
