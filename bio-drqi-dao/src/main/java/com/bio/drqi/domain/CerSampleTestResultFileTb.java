package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 取样检测结果文件
 * @TableName cer_sample_test_result_file_tb
 */
@TableName(value ="cer_sample_test_result_file_tb")
@Data
public class CerSampleTestResultFileTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId
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
    private String uploadNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}