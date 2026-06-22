package com.bio.drqi.tc.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class TcHarvestSeedStoreApplyReqDTO {

    @NotEmpty(message = "入参缺失")
    private List<Integer> idList;

    private String taskDesc;

}
