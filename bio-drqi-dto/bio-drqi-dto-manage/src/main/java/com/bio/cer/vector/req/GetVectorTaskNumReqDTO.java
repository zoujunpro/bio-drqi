package com.bio.cer.vector.req;

import cn.hutool.json.JSONUtil;
import com.bio.cer.base.PageDTO;
import com.bio.cer.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
