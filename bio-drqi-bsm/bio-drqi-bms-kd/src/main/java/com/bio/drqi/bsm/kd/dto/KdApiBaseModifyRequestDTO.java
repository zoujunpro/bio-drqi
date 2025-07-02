package com.bio.drqi.bsm.kd.dto;

import com.bio.drqi.bsm.kd.dto.model.KdModel;
import lombok.Data;

@Data
public class KdApiBaseModifyRequestDTO<T extends KdModel> {
    private Object[] NeedUpDateFields;
    private T Model;


}
