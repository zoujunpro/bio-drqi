package com.bio.drqi.manage.step.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryStepReqDTO {

    /**步骤的关联ID*/
    @NotNull(message = "步骤关联ID缺失")
    private Integer refId;
}
