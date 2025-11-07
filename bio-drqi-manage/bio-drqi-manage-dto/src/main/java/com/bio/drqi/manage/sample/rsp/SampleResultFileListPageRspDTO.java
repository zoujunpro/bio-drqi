package com.bio.drqi.manage.sample.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class SampleResultFileListPageRspDTO {
    private Integer id;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 检测结果类型 一代测序和NGS
     */
    private String resultType;

    /**
     * 创建人
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上传编号
     */
    private String uploadNum;

    /**
     * 总数量
     */
    private Integer totalNum;

    /**
     * 有效数量
     */
    private Integer effectiveNum;

    /**
     * ngs匹配成功数量
     */
    private Integer ngsSuccessNum;

    /**
     * ngs匹配失败数量
     */
    private Integer ngsFailNum;
}
