package com.bio.cer.sample.req;

import com.bio.cer.base.PageDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SampleTestByVectorTaskReqDTO extends PageDTO {
    private Integer vectorTaskId;

}
