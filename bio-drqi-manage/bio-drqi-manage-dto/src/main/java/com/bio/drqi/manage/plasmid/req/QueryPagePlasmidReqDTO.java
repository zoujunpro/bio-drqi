package com.bio.drqi.manage.plasmid.req;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class QueryPagePlasmidReqDTO  {

    @NotNull(message = "参数缺失：vectorTaskId")
    private Integer vectorTaskId;
}
