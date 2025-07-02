package com.bio.drqi.bsm.kd.dto.base;

import lombok.Data;

@Data
public class KdApiBaseModifyRequestDTO<T extends KdModel> {
    private Object[] NeedUpDateFields;
    private T Model;


}
