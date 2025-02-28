package com.bio.drqi.manage.sample.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BioInfoPageReqDTO extends PageDTO {

    private String applyNo;

    private Integer vectorTaskId;
}
