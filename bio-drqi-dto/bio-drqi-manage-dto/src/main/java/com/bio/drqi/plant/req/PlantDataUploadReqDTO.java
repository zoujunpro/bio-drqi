package com.bio.drqi.plant.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class PlantDataUploadReqDTO {
    /**
     * 文件
     */
    private MultipartFile file;

    @NotNull(message = "项目ID必填")
    private Integer projectId;
}
