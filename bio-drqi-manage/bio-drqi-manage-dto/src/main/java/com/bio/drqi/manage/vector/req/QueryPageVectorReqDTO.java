package com.bio.drqi.manage.vector.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class QueryPageVectorReqDTO extends PageDTO {
    /**项目ID*/
   private Integer projectId;

    /**
     * 载体构建任务编码
     */
    private String vectorTaskCode;

    /**
     * 项目编码
     */
    private String projectCode;

    /**
     * 子项目编码
     */
    private String subProjectCode;

    /**
     * 编辑类型  1 KO，2点突变，3精准小，4精准大
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

    private String speciesCode;

    private String breedCode;

    private String targetFlag;





}
