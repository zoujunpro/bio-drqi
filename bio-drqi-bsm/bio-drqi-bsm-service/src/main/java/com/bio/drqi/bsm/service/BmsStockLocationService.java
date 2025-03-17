package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.rsp.BmsStockLocationQueryByUnitRspDTO;

import java.util.List;

public interface BmsStockLocationService {

    List<BmsStockLocationQueryByUnitRspDTO> queryByUnit( String unitCode);
}
