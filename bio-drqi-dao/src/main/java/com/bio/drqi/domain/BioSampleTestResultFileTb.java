package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bio.common.core.context.SecurityContextHolder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 取样检测结果文件
 * @TableName bio_sample_test_result_file_tb
 */
@TableName(value ="bio_sample_test_result_file_tb")
@Data
public class BioSampleTestResultFileTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}