package com.bio.drqi.manage.plasmid.rsp;


import lombok.Data;

@Data
public class QueryPlasmidRspDTO {

    /**载体主键ID*/
    private Integer vectorId;

    /**子项目编号*/
    private String vectorCode;

    /**质粒名称*/
    private String plasmidName;



}
