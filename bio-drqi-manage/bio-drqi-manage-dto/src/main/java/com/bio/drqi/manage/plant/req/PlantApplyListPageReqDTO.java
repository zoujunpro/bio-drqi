package com.bio.drqi.manage.plant.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NotBlank
public class PlantApplyListPageReqDTO extends PageDTO {

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 试验类型 1供试  2分离提存 3扩繁  4法规测试
     */
    private String experimentType;


    /**
     * 种植申请编号
     */
    private String plantApplyNum;


    /**
     * 试验方案
     */
    private String vectorTaskCode;


    private String pdNum;
}
