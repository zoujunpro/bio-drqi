package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BmsSynKdListPageRspDTO {
    private Integer id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人
     */
    private String createUserName;

    /**
     * 同步状态 syn success fail
     */
    private String synStatus;

    /**
     * 同步失败原因
     */
    private String failReason;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 起始日期
     */
    private String beginDate;

    /**
     * 结束日期
     */
    private String endDate;

}
