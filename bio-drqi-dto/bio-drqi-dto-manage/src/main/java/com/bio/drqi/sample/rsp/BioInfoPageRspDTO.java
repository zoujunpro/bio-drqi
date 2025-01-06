package com.bio.drqi.sample.rsp;

import com.bio.drqi.base.PageDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BioInfoPageRspDTO {

    private Integer id;


    /**
     * 载体任务ID
     */
    private Integer vectorTaskId;


    /**
     * 载体任务编码
     */
    private String vectorTaskCode;

    /**
     * 质粒名称
     */
    private String plasmidName;

    /**
     * 转化编号/种子编号
     */
    private String transformCode;

    /**
     * 取样编号
     */
    private String sampleCode;

    private String remark;


    private List<BioInfoResult> bioInfoResultList = new ArrayList<>();


    @Data
    public static class BioInfoResult {
        private String sampleId;
        private String varType;
        private String mutate;
        private String ratio;
    }

    public void addBioInfoResultToList(String sampleId, String varType, String mutate, String ratio) {
        BioInfoResult bioInfoResult = new BioInfoResult();
        bioInfoResult.setSampleId(sampleId);
        bioInfoResult.setVarType(varType);
        bioInfoResult.setMutate(mutate);
        bioInfoResult.setRatio(ratio);
        this.bioInfoResultList.add(bioInfoResult);

    }

}
