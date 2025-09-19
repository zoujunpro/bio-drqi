package com.bio.drqi.manage.vector.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VectorTaskModifyVectorTaskCodeReqDTO {

    @NotNull(message = "ID参数缺失")
    private Integer id;

    @NotNull(message = "新实施方案编号缺失")
    private String vectorTaskCode;
}
