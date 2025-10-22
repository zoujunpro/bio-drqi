package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

@Data
public class TcPollinationListPageDetailReqDTO extends PageDTO {

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;
}
