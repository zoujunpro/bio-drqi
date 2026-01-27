package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BmsSynKdExecuteReqDTO {

    @NotBlank(message = "开始时间必填")
    private String beginDate;

    private String endDate;
}
