package com.bio.cer.vector.req;

import com.bio.cer.base.PageDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryPageVectorReqDTO extends PageDTO {
    /**项目ID*/
   private Integer projectId;

}
