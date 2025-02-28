package com.bio.drqi.manage.plant.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PlantListPageReqDTO extends PageDTO {
    @NotNull(message = "项目ID必填")
    private Integer projectId;
}
