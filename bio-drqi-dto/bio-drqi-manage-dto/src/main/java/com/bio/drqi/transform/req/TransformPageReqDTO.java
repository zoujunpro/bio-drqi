package com.bio.drqi.transform.req;

import com.bio.drqi.base.PageDTO;
import lombok.Data;

@Data
public class TransformPageReqDTO extends PageDTO {


    /**实施方案ID*/
    private Integer vectorTaskId;
}
