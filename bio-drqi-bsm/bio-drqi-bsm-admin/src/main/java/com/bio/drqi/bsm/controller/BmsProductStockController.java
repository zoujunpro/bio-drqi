package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsProductStockEditDateReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockMoveStockReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockQueryListReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockQueryListRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 库存明细管理
 */
@RestController
@RequestMapping("/productStock")
public class BmsProductStockController {

    @Resource
    private BmsProductStockService bmsProductStockService;

    /**
     * 库存明细管理-分页查询
     *
     * @param bmsProductStockListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "库存明细管理-分页查询")
    @RequirePermissions("bms:productStock:listPage")
    public ResponseResult<PageInfo<BmsProductStockListPageRspDTO>> listPage(@RequestBody BmsProductStockListPageReqDTO bmsProductStockListPageReqDTO) {

        return ResponseResult.getSuccess(bmsProductStockService.listPage(bmsProductStockListPageReqDTO));
    }

    /**
     * 库存明细管理-详情
     *
     * @param id
     * @return
     */
    @GetMapping("/detail")
    @WebLog(desc = "库存明细管理-详情")
    @RequirePermissions("bms:productStock:detail")
    public ResponseResult<BmsProductStockDetailRspDTO> detail(@RequestParam Integer id) {
        return ResponseResult.getSuccess(bmsProductStockService.detail(id));
    }

    /**
     * 库存明细管理-查询库存中所有商品信息
     *
     * @param unitCode
     * @return
     */
    @GetMapping("/queryStockByUnitCode")
    @WebLog(desc = "库存明细管理-查询库存中所有商品信息")
    public ResponseResult<List<String>> queryStockByUnitCode(@RequestParam String unitCode) {
        return ResponseResult.getSuccess(bmsProductStockService.queryStockByUnitCode(unitCode));
    }

    /**
     * 库存明细管理-条件查询码库存明细列表
     *
     * @param bmsProductStockQueryListReqDTO
     * @return
     */
    @PostMapping("/queryList")
    @WebLog(desc = "库存明细管理-条件查询码库存明细列表")
    public ResponseResult<List<BmsProductStockQueryListRspDTO>> queryList(@RequestBody BmsProductStockQueryListReqDTO bmsProductStockQueryListReqDTO) {

        return ResponseResult.getSuccess(bmsProductStockService.queryList(bmsProductStockQueryListReqDTO));
    }


    /**
     * 库存明细管理-更新日期
     *
     * @param bmsProductStockEditDateReqDTO
     * @return
     */
    @PostMapping("/editDate")
    @WebLog(desc = "库存明细管理-更新日期")
    @RequirePermissions("bms:productStock:editDate")
    public ResponseResult<String> editDate(@RequestBody BmsProductStockEditDateReqDTO bmsProductStockEditDateReqDTO) {
        bmsProductStockService.editDate(bmsProductStockEditDateReqDTO);
        return ResponseResult.getSuccess(null);
    }


    /**
     * 库存明细管理-调拨
     *
     * @param bmsProductStockMoveStockReqDTO
     * @return
     */
    @PostMapping("/moveStock")
    @WebLog(desc = "库存明细管理-调拨")
    public ResponseResult<String> moveStock(@RequestBody @Validated BmsProductStockMoveStockReqDTO bmsProductStockMoveStockReqDTO) {
        bmsProductStockService.moveStock(bmsProductStockMoveStockReqDTO);
        return ResponseResult.getSuccess("ok");

    }

}
