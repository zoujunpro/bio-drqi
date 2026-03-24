package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

@Data
public class TcHarvestApplyListPageReqDTO extends PageDTO {
     private String harvestApplyNum;

     private String experimentNum;
}
