package com.bio.cer.transform.req;

import com.bio.cer.base.PageDTO;
import lombok.Data;

@Data
public class TransformPageReqDTO extends PageDTO {


    /**实施方案ID*/
    private Integer vectorTaskId;
}
