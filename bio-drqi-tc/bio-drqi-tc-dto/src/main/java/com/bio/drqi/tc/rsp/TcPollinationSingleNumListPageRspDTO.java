package com.bio.drqi.tc.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 授粉单株编号分页查询响应 DTO
 */
@Data
public class TcPollinationSingleNumListPageRspDTO {

    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 试验编号
     */
    private String experimentNum;

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 单株编号
     */
    private String tcSingleNumber;

    /**
     * 分子取样编号
     */
    private String sampleCode;

    /**
     * 取样申请编号
     */
    private String sampleApplyNum;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人
     */
    private String createUserName;
}