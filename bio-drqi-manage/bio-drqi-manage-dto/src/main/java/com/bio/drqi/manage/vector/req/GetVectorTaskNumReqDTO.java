package com.bio.drqi.manage.vector.req;

import lombok.Data;

@Data
public class GetVectorTaskNumReqDTO {
    /**子项目ID*/
   private Integer subProjectId;


    /**
     * 实施方案ID
     */
   private String vectorTaskCode;

   private String ifLetter;


}
