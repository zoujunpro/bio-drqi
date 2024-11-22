package com.bio.drqi.applet.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SeedlingRemoveReqDTO {
    /**
     * 实施方案编号
     */
    @NotBlank(message = "参数缺失：实施方案编号")
    private String vectorTaskCode;
    /**
     * 取样编号
     */
    @NotBlank(message = "参数缺失：取样编号")
    private String sampleCode;


    private List<String> pictureUrls;

    private String remark;
}
