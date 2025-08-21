package com.bio.drqi.manage.dto.seed;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class SeedOutDTO extends SeedProcDTO {


    /**
     * 申请表单
     */
    private ApplyFrom applyFrom;



    /**
     * 执行表单
     */
    private ExecuteForm executeForm;


    @Data
    public static class ApplyFrom {

        /**
         * 用途
         */
        private String useToDesc;
        /**
         * 出库类型 对内，对外
         */
        private String outType;

        /**
         * 种子要求
         */
        private String seedDemandDesc;

        /**
         * 分装和标签要求
         */
        private String labelDemandDesc;
        /**
         * 申请备注
         */
        private String applyRemark;

        /**
         * 交付方式 1邮寄 2自提
         */
        private String deliverMethod;

        private String receiverTelephone;

        private String receiverAddress;

        private String receiverName;


        private List<ApplyFromContent> applyFromContentList;
    }


    @Data
    public static class ApplyFromContent {

        private String seedNum;
        private String projectCode;
        private String projectName;
        /**
         * 预览 返显用
         */
        private String subProjectCode;
        /**
         * 预览 返显用
         */
        private String vectorTaskCode;

        private String geneType;

        private String breedName;
        private String breedCode;


        private String speciesName;
        private String speciesCode;

        private String productAddress;

        private String year;

        private String sgr;

        private String tpur;

        private String num;

        private String unit;

        private String coatingFlag;

        private String remark;

    }

    @Data
    public static class ExecuteForm {
        private List<ExecuteFormContent> executeFormContentList=new ArrayList<>();
    }


    @Data
    public static class ExecuteFormContent {
        private String seedNum;
        private String num;
        private String remark;
    }

    public static void main(String[] args) {
        SeedOutDTO seedOutDTO=new SeedOutDTO();



        ApplyFrom applyF=new ApplyFrom();
        applyF.setOutType("用途");
        applyF.setApplyRemark("备注");
        applyF.setDeliverMethod("交付方式 邮寄 自提");
        applyF.setReceiverTelephone("接收人电话");
        applyF.setReceiverAddress("接收人地址");
        applyF.setReceiverName("接收人姓名");


        ApplyFromContent applyFromContent=new ApplyFromContent();
        applyFromContent.setProjectCode("项目编号");
        applyFromContent.setProjectName("项目名称");
        applyFromContent.setSubProjectCode("子项目编号");
        applyFromContent.setVectorTaskCode("载体任务编号");
        applyFromContent.setGeneType("基因类型");
        applyFromContent.setBreedName("品种");
        applyFromContent.setProductAddress("产地");
        applyFromContent.setYear("年份");
        applyFromContent.setSgr("发芽率");
        applyFromContent.setTpur("性状纯度");
        applyFromContent.setNum("出库数量");
        applyFromContent.setUnit("单位");
        applyFromContent.setCoatingFlag("是否包衣 是/否");
        applyFromContent.setRemark("备注");
        applyF.setApplyFromContentList(Arrays.asList(applyFromContent));
        seedOutDTO.setApplyFrom(applyF);

        ExecuteForm executeForm1=new ExecuteForm();

        ExecuteFormContent executeFormContent=new ExecuteFormContent();
        executeFormContent.setSeedNum("111");
        executeFormContent.setNum("kg");

        executeForm1.setExecuteFormContentList(Arrays.asList(executeFormContent));
        seedOutDTO.setExecuteForm(executeForm1);
        System.out.println(JSONUtil.toJsonStr(seedOutDTO));

    }


}
