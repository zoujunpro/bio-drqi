package com.bio.cer.seed;

import com.bio.cer.base.PageDTO;
import lombok.Data;

@Data
public class SeedStockInReqDTO extends PageDTO {
    private String seedNum;
    private String sourceType;
    private String taskNum;

}
