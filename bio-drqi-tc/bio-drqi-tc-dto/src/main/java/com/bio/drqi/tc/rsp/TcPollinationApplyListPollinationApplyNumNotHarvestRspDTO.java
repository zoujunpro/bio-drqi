package com.bio.drqi.tc.rsp;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TcPollinationApplyListPollinationApplyNumNotHarvestRspDTO {
    /**
     * 授粉方式
     */
    private String pollinationTypeName;

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 创建人
     */
    private String createUserName;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
}
