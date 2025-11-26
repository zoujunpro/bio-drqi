package com.bio.drqi.manage.bio.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BioSampleTestQueryBySampleCodeListReqDTO {

    @NotEmpty(message = "取样编号缺失")
    private List<String> sampleCodeList;
}
