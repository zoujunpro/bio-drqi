package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsProductStockOutLogListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockOutLogDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockOutLogListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockOutService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 出库存明细管理
 */
@RestController
@RequestMapping("/productStockIn")
public class BmsProductStockOutLogController {

    @Resource
    private BmsProductStockOutService bmsProductStockOutService;

    /**
     * 出库存明细管理-分页查询
     *
     * @param bmsProductStockOutLogListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "出库存明细管理-分页查询")
    public ResponseResult<PageInfo<BmsProductStockOutLogListPageRspDTO>> listPage(@RequestBody BmsProductStockOutLogListPageReqDTO bmsProductStockOutLogListPageReqDTO) {
        return ResponseResult.getSuccess(bmsProductStockOutService.listPage(bmsProductStockOutLogListPageReqDTO));

    }

    /**
     * 出库存明细管理-详情
     *
     * @param id
     * @return
     */
    @GetMapping("/detail")
    @WebLog(desc = "出库存明细管理-详情")
    public ResponseResult<BmsProductStockOutLogDetailRspDTO> detail(@RequestParam Integer id) {
        return ResponseResult.getSuccess(bmsProductStockOutService.detail(id));
    }
}
