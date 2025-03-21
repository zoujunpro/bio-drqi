package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsProductStockOutLogListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockOutLogDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockOutLogListPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface BmsProductStockOutService {


    /**
     * 出库存明细管理-分页查询
     *
     * @param bmsProductStockOutLogListPageReqDTO
     * @return
     */
    PageInfo<BmsProductStockOutLogListPageRspDTO> listPage(BmsProductStockOutLogListPageReqDTO bmsProductStockOutLogListPageReqDTO);

    /**
     * 出库存明细管理-详情
     *
     * @param id
     * @return
     */
    BmsProductStockOutLogDetailRspDTO detail(Integer id);
}
