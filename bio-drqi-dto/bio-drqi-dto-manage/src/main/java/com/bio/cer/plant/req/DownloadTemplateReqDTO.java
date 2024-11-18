package com.bio.cer.plant.req;

import com.bio.cer.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Data
public class DownloadTemplateReqDTO {
    @NotNull(message = "参数缺失：subProjectId")
    private Integer subProjectId;

    @NotNull(message = "参数缺失：vectorTaskId")
    private Integer vectorTaskId;



}
