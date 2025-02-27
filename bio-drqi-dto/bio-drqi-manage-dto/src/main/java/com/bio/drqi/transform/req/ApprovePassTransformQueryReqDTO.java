package com.bio.drqi.transform.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ApprovePassTransformQueryReqDTO {

    @NotBlank(message = "实施方案编码参数缺失")
    private String vectorTaskCode;


}
