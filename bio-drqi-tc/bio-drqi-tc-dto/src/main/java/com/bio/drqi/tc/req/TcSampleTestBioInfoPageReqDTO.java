package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

@Data
public class TcSampleTestBioInfoPageReqDTO extends PageDTO {

    private String applyNo;

    private Integer vectorTaskId;
}
