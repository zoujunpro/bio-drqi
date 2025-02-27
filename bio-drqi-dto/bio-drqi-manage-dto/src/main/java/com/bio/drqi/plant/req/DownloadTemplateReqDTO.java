package com.bio.drqi.plant.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DownloadTemplateReqDTO {
    @NotNull(message = "参数缺失：subProjectId")
    private Integer subProjectId;

    @NotNull(message = "参数缺失：vectorTaskId")
    private Integer vectorTaskId;



}
