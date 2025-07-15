package com.bio.drqi.bsm.kd.service;


import com.bio.drqi.bsm.kd.dto.KdDTO;
import com.bio.drqi.bsm.kd.dto.QuerySupplierDTO;
import com.bio.drqi.bsm.kd.dto.model.KdModel;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;
import com.bio.drqi.bsm.kd.enums.OperateEnum;

import java.util.List;

public interface KdApiService {
    String execute(OperateEnum operateEnum, Object Obj, String unitCode);

    List<QuerySupplierDTO> querySupplier();


}
