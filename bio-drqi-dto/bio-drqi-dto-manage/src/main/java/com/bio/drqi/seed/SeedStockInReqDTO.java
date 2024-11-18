package com.bio.drqi.seed;

import com.bio.drqi.base.PageDTO;
import lombok.Data;

@Data
public class SeedStockInReqDTO extends PageDTO {
    private String seedNum;
    private String sourceType;
    private String taskNum;

}
