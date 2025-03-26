package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsProductStockInLogListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogQueryByTaskNumRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;


public interface BmsProductStockInService {

    PageInfo<BmsProductStockInLogListPageRspDTO> listPage(BmsProductStockInLogListPageReqDTO bmsProductStockInLogListPageReqDTO);

    /**
     * 入库存明细管理-详情
     *
     * @return
     */
    BmsProductStockInLogDetailRspDTO detail(Integer id);

    List<BmsProductStockInLogQueryByTaskNumRspDTO> queryByTaskNum(String taskNum);

}
