package com.bio.drqi.manage.dto.seed;

import com.bio.drqi.common.dto.BaseBioTaskDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SeedQualityCheckDTO extends BaseBioTaskDTO {
    @NotBlank(message = "文件不能为空")
    private String excelUrl;
}
