package com.bio.drqi.manage.vector.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class VectorListPageRspDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 载体构建任务编码
     */
    private String vectorTaskCode;

    /**
     * 载体构建任务类型 1测试任务（普通），2测试任务（原生质体），3正常任务创建
     */
    private String vectorTaskType;

    /**
     * 载体构建任务名称
     */
    private String vectorTaskName;

    /**
     * 建议递送方式
     */
    private String deliveryMethod;

    /**
     * 受体材料
     */
    private String acceptorMaterial;

    private List<AcceptorMaterialModel> acceptorMaterialModelList = new ArrayList<>();

    /**
     * 建议编辑工具
     */
    private String editTools;


    private String speciesCode;

    private String wordUrl;
    /**
     * 工具类型
     */
    private String editToolsType;

    /**
     * 载体构建任务目标
     */
    private String vectorTaskTarget;

    /**
     * 备注（实验方案）
     */
    private String remark;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

    /**
     * 项目编码
     */
    private String projectCode;

    /**
     * 子项目编码
     */
    private String subProjectCode;

    /**
     * 基因编辑方式 1转基因 2基因编辑
     */
    private String geneEditMethod;

    /**
     * 编辑类型
     */
    private String editType;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 任务状态 1审批中 2审批通过，3审批拒绝
     */
    private String taskStatus;

    /**
     * 质质检结果  空未质检 pass已通过 refuse未通过
     */
    private String qualityInspectionResult;

    private List<Vector> children;

    private List<VectorGroup> vectorGroupList;


    @Data
    public static class VectorGroup {

        private String groupName;

        private String plasmidNames;

        private String remark;
        private Integer repeatNum;

        public VectorGroup(String groupName, String plasmidNames, String remark, Integer repeatNum) {
            this.groupName = groupName;
            this.plasmidNames = plasmidNames;
            this.repeatNum = repeatNum;
            this.remark = remark;
        }
    }

    @Data
    public static class Vector {
        /**
         * 主键ID
         */
        private Integer id;

        /**
         * 载体构建任务
         */
        private Integer vectorTaskId;

        /**
         * 质粒名称
         */
        private String plasmidName;

        /**
         * 靶位点
         */
        private String targetSite;

        /**
         * 细菌抗性
         */
        private String bacterialResistance;

        /**
         * 质粒特异性引物
         */
        private String plasmidSpecificPrimers;

        /**
         * 细菌复制子
         */
        private String bacterialReplicon;

        /**
         * 拷贝数
         */
        private String copyNumber;

        /**
         * 农杆菌信息
         */
        private String agrobacteriumInformation;

        /**
         * 植物筛选标记
         */
        private String selectionMarker;

        /**
         * 外源基因
         */
        private String foreignGene;

        /**
         * 目标特性
         */
        private String geneCharacter;

        /**
         * 靶基因
         */
        private String targetGene;

        /**
         * PAM
         */
        private String pam;

        /**
         * 备注
         */
        private String remark;

        private String fileUrls;

        /**
         * 期望阳性苗
         */
        private Integer expectedPositiveVaccine;

        /**
         * 质质检结果  空未质检 pass已通过 refuse未通过
         */
        private String qualityInspectionResult;


        /**
         * 目的条带大小
         */
        private String destinationStripeSize;

        /**
         * 载体大小
         */
        private String vectorSize;
    }

    @Data
    private class AcceptorMaterialModel {
        private String acceptorMaterialName;
        private String acceptorMaterialCode;

    }

    public void buildAcceptorMaterialModel(String acceptorMaterialCode, String acceptorMaterialName) {
        AcceptorMaterialModel acceptorMaterialModel = new AcceptorMaterialModel();
        acceptorMaterialModel.setAcceptorMaterialName(acceptorMaterialCode);
        acceptorMaterialModel.setAcceptorMaterialCode(acceptorMaterialName);
        this.acceptorMaterialModelList.add(acceptorMaterialModel);
    }

}
