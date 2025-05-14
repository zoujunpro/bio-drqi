package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

@Data
public class TcPollinationListPageDetailReqDTO extends PageDTO {

    /**
     * 实验编号
     */
    private String experimentNum;

}
