package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsStockLocationAddReqDTO;
import com.bio.drqi.bsm.req.BmsStockLocationEditReqDTO;
import com.bio.drqi.bsm.req.BmsStockLocationListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockLocationListAllStockRspDTO;
import com.bio.drqi.bsm.rsp.BmsStockLocationListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface BmsStockLocationService {



    /**
     * 库房管理-分页查询
     *
     * @param bmsStockLocationListPageReqDTO
     * @return
     */
    PageInfo<BmsStockLocationListPageRspDTO> listPage(@RequestBody BmsStockLocationListPageReqDTO bmsStockLocationListPageReqDTO);

    /**
     * 库房管理-新增
     *
     * @param bmsStockLocationAddReqDTO
     * @return
     */
    void add(BmsStockLocationAddReqDTO bmsStockLocationAddReqDTO);

    /**
     * 库房管理-删除
     *
     * @param id
     * @return
     */
    void delete(Integer id);

    /**
     * 库房管理-编辑
     *
     * @param bmsStockLocationEditReqDTO
     * @return
     */
    void edit(BmsStockLocationEditReqDTO bmsStockLocationEditReqDTO);

    List<BmsStockLocationListAllStockRspDTO> listAllStock();
}
