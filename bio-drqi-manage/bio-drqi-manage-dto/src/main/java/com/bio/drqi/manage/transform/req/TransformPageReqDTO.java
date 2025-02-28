package com.bio.drqi.manage.transform.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class TransformPageReqDTO extends PageDTO {


    /**实施方案ID*/
    private Integer vectorTaskId;
}
