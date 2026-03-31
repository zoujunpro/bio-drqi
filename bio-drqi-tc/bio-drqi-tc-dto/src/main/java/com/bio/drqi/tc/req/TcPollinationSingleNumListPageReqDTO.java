package com.bio.drqi.tc.req;

import lombok.Data;
/**
 * 授粉单株编号分页查询请求 DTO
 */
@Data
public class TcPollinationSingleNumListPageReqDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

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
}
