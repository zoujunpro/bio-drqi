package com.bio.drqi.manage.vector.req;


import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class CerVectorBuildListPageReqDTO extends PageDTO {

    private String vectorTaskCode;

    /**
     * 质粒名称
     */
    private String plasmidName;


    private String taskNum;

    private String transFlag;



}
