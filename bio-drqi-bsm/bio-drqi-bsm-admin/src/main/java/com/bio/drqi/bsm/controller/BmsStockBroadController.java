package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsStockBroadCountStockReqDTO;
import com.bio.drqi.bsm.rsp.*;
import com.bio.drqi.bsm.service.BmsStockBroadService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 库存数据统计
 */
@RestController
@RequestMapping("bmsStockBroad")
public class BmsStockBroadController {

    @Resource
    private BmsStockBroadService bmsStockBroadService;

    /**
     * 库存数据统计-总量统计
     *
     * @return
     */
    @PostMapping("countStock")
    @WebLog(desc = "库存数据统计-总量统计")
    public ResponseResult<BmsStockBroadCountStockRspDTO> countStock(@RequestBody BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        return ResponseResult.getSuccess(bmsStockBroadService.countStock(bmsStockBroadCountStockReqDTO));
    }
    /**
     * 库存数据统计-统计出入库明细
     *
     * @return
     */
    @PostMapping("countStockDetailList")
    @WebLog(desc = "库存数据统计-统计出入库明细")
    public ResponseResult<List<BmsStockBroadCountStockDetailListRspDTO>> countStockDetailList(@RequestBody  BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        return ResponseResult.getSuccess(bmsStockBroadService.countStockDetailList(bmsStockBroadCountStockReqDTO));
    }

    /**
     * 库存数据统计-按类别统计
     *
     * @return
     */
    @PostMapping("countStockByCategory")
    @WebLog(desc = "库存数据统计-按类别统计")
    public ResponseResult<List<BmsStockBroadCountByCategoryRspDTO>> countStockByCategory(@RequestBody  BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        return ResponseResult.getSuccess(bmsStockBroadService.countStockByCategory(bmsStockBroadCountStockReqDTO));
    }


    /**
     * 入库存数据统计-按类别统计
     *
     * @return
     */
    @PostMapping("countStockInByCategory")
    @WebLog(desc = "入库存数据统计-按类别统计")
    public ResponseResult<List<BmsStockInBroadCountByCategoryRspDTO>> countStockInByCategory(@RequestBody BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        return ResponseResult.getSuccess(bmsStockBroadService.countStockInByCategory(bmsStockBroadCountStockReqDTO));
    }

    /**
     * 出库存数据统计-按类别统计
     *
     * @return
     */
    @PostMapping("countStockOutByCategory")
    @WebLog(desc = "出库存数据统计-按类别统计")
    public ResponseResult<List<BmsStockOutBroadCountByCategoryRspDTO>> countStockOutByCategory(@RequestBody BmsStockBroadCountStockReqDTO bmsStockBroadCountStockReqDTO) {
        return ResponseResult.getSuccess(bmsStockBroadService.countStockOutByCategory(bmsStockBroadCountStockReqDTO));
    }

}
