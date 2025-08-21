package com.bio.drqi.applet.dto.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ScanCodeT0PlantTestRspDTO {

    private String projectCode;

    private String projectName;

    private String subProjectCode;

    private String vectorTaskCode;

    private String vectorTaskName;


    private PlantDtlInfo plantDtlInfo;

    private List<SampleInfo> sampleInfoList;

    @Data
    public static class PlantDtlInfo{
        /**
         * 种子编号
         */
        private String plantCode;


        /**
         * 转化组合名称
         */
        private String transformGroupName;


        /**
         * 质粒信息
         */
        private String plasmidName;

        /**
         * 转化编号
         */
        private String transformCode;

        /**
         * 取样编号
         */
        private String sampleCode;

        /**
         * 代次
         */
        private String generation;

        /**
         * 株树
         */
        private Integer plantNumber;

        /**
         * 播种/移苗日期
         */
        private String plantDate;

        /**
         * 移栽日期
         */
        private String transplantDate;

        /**
         * 春化开始日期
         */
        private String vernalizationBeginDate;

        /**
         * 春化结束日期
         */
        private String vernalizationEndDate;

        /**
         * 授粉方式
         */
        private String pollinationMethod;

        /**
         * 植株状态 1正常，异常
         */
        private String plantStatus;

        /**
         * 父本信息
         */
        private String fatherInfo;

        /**
         * 母本信息
         */
        private String motherInfo;

        /**
         * 授粉时间
         */
        private String pollinationDate;

        /**
         * 收获日期
         */
        private String harvestDate;

        /**
         * 其他字段
         */
        private Object otherField;

        /**
         * 编辑类型
         */
        private String editType;

        /**
         * 项目物种
         */
        private String speciesCode;

        /**
         * 受体材料
         */
        private String acceptorMaterial;

        /**
         * 创建日期
         */
        private Date createDate;

        /**
         * 更新日期
         */
        private Date updateTime;


        private Integer createUserId;

        private String createUserName;

    }



    @Data
    public static class SampleInfo{
        /**
         * 受体材料
         */
        private String acceptorMaterial;

        /**
         * 取样编号
         */
        private String sampleCode;

        /**
         * 取样申请时间
         */
        private Date applyTime;

        /**
         * 取样申请人ID
         */
        private Integer applyUserId;

        /**
         * 取样申请人姓名
         */
        private String applyUserName;

        /**
         * 取样时间
         */
        private String sampleTime;

        /**
         * 取样备注
         */
        private String sampleRemark;

        /**
         * 代次
         */
        private String sampleGeneration;


        /**
         * 取样数据递送人姓名
         */
        private String sampleUserName;

        /**
         * 取样数据递送人ID
         */
        private Integer sampleUserId;

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
         * 审核人姓名
         */
        private String checkUserName;

        /**
         * 审核人ID
         */
        private Integer checkUserId;

        /**
         * 审查结果
         */
        private String checkResult;

        /**
         * 创建日期
         */
        private Date createTime;

        /**
         * 更新日期
         */
        private Date updateTime;

        private String applyNo;

        private String identifyPrimer;
    }

}
