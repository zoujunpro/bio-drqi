package com.bio.cer.transform.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ApprovePassTransformQueryReqDTO {

    @NotBlank(message = "实施方案编码参数缺失")
    private String vectorTaskCode;


}
