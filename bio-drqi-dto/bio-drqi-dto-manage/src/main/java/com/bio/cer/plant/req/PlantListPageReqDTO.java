package com.bio.cer.plant.req;

import com.bio.cer.base.PageDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PlantListPageReqDTO extends PageDTO {
    @NotNull(message = "项目ID必填")
    private Integer projectId;
}
