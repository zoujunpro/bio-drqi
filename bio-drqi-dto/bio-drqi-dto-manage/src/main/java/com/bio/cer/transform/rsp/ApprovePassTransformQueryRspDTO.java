package com.bio.cer.transform.rsp;

import lombok.Data;

@Data
public class ApprovePassTransformQueryRspDTO {
    /**载体编码*/
    private String vectorTaskCode;
    /**子项目名称*/
    private String subProjectCode;
    /**转化编号*/
    private String transformCode;
    /**质粒名称/共转质粒*/
    private String plasmidName;
    /**受体材料*/
    private String acceptorMaterial;




}
