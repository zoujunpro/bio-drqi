package com.bio.drqi.tc.req;

import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class TcHavestDownSeedStockInExcelReqDTO {

    @NotEmpty(message = "入参缺失")
    private List<Integer> idList;
}
