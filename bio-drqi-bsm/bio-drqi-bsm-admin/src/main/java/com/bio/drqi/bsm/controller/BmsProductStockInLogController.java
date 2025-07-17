package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsProductStockInLogListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockInLogReturnStockReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogQueryByTaskNumRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockInService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 入库存明细管理
 */
@RestController
@RequestMapping("/productStockInLog")
public class BmsProductStockInLogController {


    @Resource
    private BmsProductStockInService bmsProductStockInService;

    /**
     * 入库存明细管理-分页查询
     *
     * @param bmsProductStockInLogListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "入库存明细管理-分页查询")
    @RequirePermissions("bms:productStockInLog:listPage")
    public ResponseResult<PageInfo<BmsProductStockInLogListPageRspDTO>> listPage(@RequestBody BmsProductStockInLogListPageReqDTO bmsProductStockInLogListPageReqDTO) {
        return ResponseResult.getSuccess(bmsProductStockInService.listPage(bmsProductStockInLogListPageReqDTO));
    }

    /**
     * 入库存明细管理-详情
     *
     * @return
     */
    @GetMapping("/detail")
    @WebLog(desc = "入库存明细管理-详情")
    @RequirePermissions("bms:productStockInLog:detail")
    public ResponseResult<BmsProductStockInLogDetailRspDTO> detail(@RequestParam Integer id) {
        return ResponseResult.getSuccess(bmsProductStockInService.detail(id));
    }


    /**
     * 入库存明细管理-根据任务号查询入库明细
     *
     * @param taskNum
     * @return
     */

    @GetMapping("queryByTaskNum")
    @WebLog(desc = "入库存明细管理-根据任务号查询入库明细")
    public ResponseResult<List<BmsProductStockInLogQueryByTaskNumRspDTO>> queryByTaskNum(@RequestParam String taskNum) {
        return ResponseResult.getSuccess(bmsProductStockInService.queryByTaskNum(taskNum));

    }

    /**
     * 入库存明细管理-退货
     * @param bmsProductStockInLogReturnStockReqDTO
     * @return
     */
    @PostMapping("returnStock")
    @WebLog(desc = "入库存明细管理-退货")
    public ResponseResult<String> returnStock(@RequestBody @Validated BmsProductStockInLogReturnStockReqDTO bmsProductStockInLogReturnStockReqDTO) {
        bmsProductStockInService.returnStock(bmsProductStockInLogReturnStockReqDTO);
        return ResponseResult.getSuccess("ok");
    }
}
