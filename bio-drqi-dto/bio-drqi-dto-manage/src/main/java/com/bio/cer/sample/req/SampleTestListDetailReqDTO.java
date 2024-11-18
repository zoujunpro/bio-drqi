package com.bio.cer.sample.req;

import com.bio.cer.base.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class SampleTestListDetailReqDTO extends PageDTO {


    private String applyNo;

    private Integer vectorTaskId;


}
