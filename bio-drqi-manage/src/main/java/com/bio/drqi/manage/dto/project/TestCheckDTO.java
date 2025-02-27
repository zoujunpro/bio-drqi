package com.bio.drqi.manage.dto.project;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class TestCheckDTO {



    @NotNull(message = "审核数据内容缺失")
    @Valid
    private List<CheckContent> contentList=new ArrayList<>();

    @Data
    public static class CheckContent{

        @NotBlank(message = "实施方案编号缺失")
        private String vectorTaskCode;

        @NotBlank(message = "取样编号缺失")
        private String sampleCode;

        private String checkResult;
        /**
         * 质粒名称
         */
        private String plasmidName;
        /**
         * 转化编号/种子编号
         */
        private String transformCode;
        /**
         * 取样备注
         */
        private String sampleRemark;

        /**
         * 取样时间
         */
        private String sampleTime;
        /**
         * 代次
         */
        private String sampleGeneration;
        /**
         * 鉴定引物
         */
        private String testIdentifyPrimer;

        /**
         * 检测方法
         */
        private String testMethod;

        /**
         * 编辑特性
         */
        private String testEditFeature;

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




    }

}
