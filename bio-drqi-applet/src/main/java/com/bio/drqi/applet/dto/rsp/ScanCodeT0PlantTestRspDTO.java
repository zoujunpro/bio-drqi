package com.bio.drqi.applet.dto.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ScanCodeT0PlantTestRspDTO {

    private String projectCode;

    private String projectName;

    private String subProjectCode;

    private String subProjectName;


    private String vectorTaskCode;

    private String vectorTaskName;


    private PlantDtlInfo plantDtlInfo;

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

}
