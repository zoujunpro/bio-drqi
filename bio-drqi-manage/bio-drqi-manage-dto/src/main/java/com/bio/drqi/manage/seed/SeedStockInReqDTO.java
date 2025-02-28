package com.bio.drqi.manage.seed;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class SeedStockInReqDTO extends PageDTO {
    private String seedNum;
    private String sourceType;
    private String taskNum;

}
