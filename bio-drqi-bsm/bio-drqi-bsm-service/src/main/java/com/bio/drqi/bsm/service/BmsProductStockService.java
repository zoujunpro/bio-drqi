package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsProductStockListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockListPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface BmsProductStockService {


    /**
     * 库存明细管理-分页查询
     *
     * @param bmsProductStockListPageReqDTO
     * @return
     */
    PageInfo<BmsProductStockListPageRspDTO> listPage(BmsProductStockListPageReqDTO bmsProductStockListPageReqDTO);

    /**
     * 库存明细管理-详情
     *
     * @param id
     * @return
     */
    BmsProductStockDetailRspDTO detail(Integer id);
}
