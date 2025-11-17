package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 一代测序结果
 *
 * @TableName bio_sample_test_one_result_tb
 */
@TableName(value = "bio_sample_test_one_result_tb")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BioSampleTestOneResultTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 鉴定引物
     */
    private String testIdentifyPrimer;

    /**
     * 检测方法
     */
    private String testMethod;

    /**
     * 编辑类型
     */
    private String testEditType;

    /**
     * 非转鉴定引物
     */
    private String testNoTransIdentityPrimer;

    /**
     * 是否为转基因阳性
     */
    private String testIsGeneModifyPositive;

    /**
     * 是否为定点插入
     */
    private String testIfFixedPoint;

    /**
     * 是否为单拷贝插入
     */
    private String testIfCopyInsert;

    /**
     * 定点插入方式（定点纯合/定点杂合）
     */
    private String testFixedPointType;

    /**
     * donor载体残留情况
     */
    private String testDonorResidueInfo;

    /**
     * 插入位点
     */
    private String testInsertionSite;

    /**
     * ELISA结果（蛋白表达量）
     */
    private String testElisaResult;

    /**
     * qbzr表达量
     */
    private String testQbzrSeq;

    /**
     * 编辑工具残留情况
     */
    private String testEditResidueInfo;

    /**
     * 检测数据递送人ID
     */
    private Integer testUserId;

    /**
     * 检测人
     */
    private String testUserName;

    /**
     * 检测时间
     */
    private String testTime;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 检测渠道 1 项目 2大田
     */
    private String testChannel;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 上传编号
     */
    private String uploadNum;

    private String remark;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public static BioSampleTestOneResultTb of(CerSampleTestTb cerSampleTestTb, String channel, String taskNum, String uploadNum) {
        BioSampleTestOneResultTb bioSampleSampleOneResultTb = new BioSampleTestOneResultTb();
        bioSampleSampleOneResultTb.setSampleCode(cerSampleTestTb.getSampleCode());
        bioSampleSampleOneResultTb.setTestIdentifyPrimer(cerSampleTestTb.getTestIdentifyPrimer());
        bioSampleSampleOneResultTb.setTestMethod(cerSampleTestTb.getTestMethod());
        bioSampleSampleOneResultTb.setTestEditType(cerSampleTestTb.getTestEditType());
        bioSampleSampleOneResultTb.setTestNoTransIdentityPrimer(cerSampleTestTb.getTestNoTransIdentityPrimer());
        bioSampleSampleOneResultTb.setTestIsGeneModifyPositive(cerSampleTestTb.getTestIsGeneModifyPositive());
        bioSampleSampleOneResultTb.setTestIfFixedPoint(cerSampleTestTb.getTestIfFixedPoint());
        bioSampleSampleOneResultTb.setTestIfCopyInsert(cerSampleTestTb.getTestIfCopyInsert());
        bioSampleSampleOneResultTb.setTestFixedPointType(cerSampleTestTb.getTestFixedPointType());
        bioSampleSampleOneResultTb.setTestDonorResidueInfo(cerSampleTestTb.getTestDonorResidueInfo());
        bioSampleSampleOneResultTb.setTestInsertionSite(cerSampleTestTb.getTestInsertionSite());
        bioSampleSampleOneResultTb.setTestElisaResult(cerSampleTestTb.getTestElisaResult());
        bioSampleSampleOneResultTb.setTestQbzrSeq(cerSampleTestTb.getTestQbzrSeq());
        bioSampleSampleOneResultTb.setTestEditResidueInfo(cerSampleTestTb.getTestEditResidueInfo());
        bioSampleSampleOneResultTb.setTestUserId(cerSampleTestTb.getTestUserId());
        bioSampleSampleOneResultTb.setTestUserName(cerSampleTestTb.getTestUserName());
        bioSampleSampleOneResultTb.setTestTime(cerSampleTestTb.getTestTime());
        bioSampleSampleOneResultTb.setCreateTime(new Date());
        bioSampleSampleOneResultTb.setTestChannel(channel);
        bioSampleSampleOneResultTb.setTaskNum(taskNum);
        bioSampleSampleOneResultTb.setUploadNum(uploadNum);
        return bioSampleSampleOneResultTb;
    }

}