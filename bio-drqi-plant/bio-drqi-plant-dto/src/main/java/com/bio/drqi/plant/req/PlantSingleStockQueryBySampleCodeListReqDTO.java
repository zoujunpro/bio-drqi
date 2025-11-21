package com.bio.drqi.plant.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class PlantSingleStockQueryBySampleCodeListReqDTO {

    @NotEmpty(message = "取样编号缺失")
    private List<String> sampleCodeList;
}
