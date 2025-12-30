package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsStockBroadCountStockReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockBroadCountStockDetailListRspDTO;
import com.bio.drqi.bsm.rsp.BmsStockBroadCountStockRspDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 库存数据统计
 */
@RestController
@RequestMapping("bmsStockBroad")
public class BmsStockBroadController {

    /**
     * 库存数据统计-总量统计
     *
     * @return
     */
    @GetMapping("countStock")
    @WebLog(desc = "库存数据统计-总量统计")
    public ResponseResult<BmsStockBroadCountStockRspDTO> countStock(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        return null;
    }
    /**
     * 库存数据统计-总量统计
     *
     * @return
     */
    @GetMapping("countStockDetailList")
    @WebLog(desc = "库存数据统计-总量统计")
    public ResponseResult<List<BmsStockBroadCountStockDetailListRspDTO>> countStockDetailList(BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        return null;
    }




}
