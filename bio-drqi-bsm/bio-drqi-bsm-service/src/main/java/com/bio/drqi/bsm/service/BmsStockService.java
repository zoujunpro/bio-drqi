package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsStockAddReqDTO;
import com.bio.drqi.bsm.req.BmsStockEditReqDTO;
import com.bio.drqi.bsm.req.BmsStockSynKdReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockQueryByUnitRspDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface BmsStockService {
    List<BmsStockQueryByUnitRspDTO> queryStockByUnit(String unitCode);


    void add(BmsStockAddReqDTO bmsStockAddReqDTO);

    void edit(BmsStockEditReqDTO bmsStockEditReqDTO);

    void delete(Integer id);


    void synKd(BmsStockSynKdReqDTO bmsStockSynKdReqDTO);


}
