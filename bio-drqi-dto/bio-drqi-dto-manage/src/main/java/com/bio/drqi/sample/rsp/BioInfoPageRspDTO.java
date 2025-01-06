package com.bio.drqi.sample.rsp;

import com.bio.drqi.base.PageDTO;
import lombok.Data;

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


    private List<BioInfoResult> bioInfoResultList;



    @Data
    public static class BioInfoResult{
        private String sampleId;
        private String varType;
        private String mutate;
        private String ratio;
    }

}
