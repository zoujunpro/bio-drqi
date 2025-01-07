package com.bio.drqi.sample.req;

import com.bio.drqi.base.PageDTO;
import lombok.Data;

@Data
public class SampleApplyListPageReqDTO extends PageDTO {

    private String applyNo;

    private String sampleCode;


}
