package com.bio.drqi.bsm.service;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsStockBroadCountStockReqDTO;
import com.bio.drqi.bsm.rsp.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface BmsStockBroadService {

    /**
     * 库存数据统计-总量统计
     *
     * @return
     */
    BmsStockBroadCountStockRspDTO countStock(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO);

    /**
     * 库存数据统计-统计出入库明细
     *
     * @return
     */
    List<BmsStockBroadCountStockDetailListRspDTO> countStockDetailList(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO);

    /**
     * 库存数据统计-按类别统计
     *
     * @return
     */
    List<BmsStockBroadCountByCategoryRspDTO> countStockByCategory(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO);


    /**
     * 入库存数据统计-按类别统计
     *
     * @return
     */
    List<BmsStockInBroadCountByCategoryRspDTO> countStockInByCategory(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO);

    /**
     * 出库存数据统计-按类别统计
     *
     * @return
     */
    List<BmsStockOutBroadCountByCategoryRspDTO> countStockOutByCategory(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO);
}
