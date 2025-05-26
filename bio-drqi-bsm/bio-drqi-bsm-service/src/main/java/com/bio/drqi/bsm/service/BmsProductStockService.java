package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsProductStockEditDateReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockQueryListReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockQueryListRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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


    List<String> queryStockByUnitCode( String unitCode);

    List<BmsProductStockQueryListRspDTO> queryList(@RequestBody BmsProductStockQueryListReqDTO bmsProductStockQueryListReqDTO);

    void editDate( BmsProductStockEditDateReqDTO bmsProductStockEditDateReqDTO);
}
