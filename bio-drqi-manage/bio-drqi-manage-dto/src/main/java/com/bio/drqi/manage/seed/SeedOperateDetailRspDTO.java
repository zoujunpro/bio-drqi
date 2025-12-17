package com.bio.drqi.manage.seed;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeedOperateDetailRspDTO {

    private String operateDesc;

    private String operateCode;

    private String taskNum;

    private String operateUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

    private String unit;

    private BigDecimal number;


}
