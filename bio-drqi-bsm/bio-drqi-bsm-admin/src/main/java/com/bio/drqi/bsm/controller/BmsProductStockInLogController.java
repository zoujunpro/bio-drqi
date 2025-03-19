package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.bsm.req.BmsProductStockInLogListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockInService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    public ResponseResult<PageInfo<BmsProductStockInLogListPageRspDTO>> listPage(@RequestBody BmsProductStockInLogListPageReqDTO bmsProductStockInLogListPageReqDTO) {
        return ResponseResult.getSuccess(bmsProductStockInService.listPage(bmsProductStockInLogListPageReqDTO));
    }

    /**
     * 入库存明细管理-详情
     *
     * @return
     */
    @GetMapping("/detail")
    public ResponseResult<BmsProductStockInLogDetailRspDTO> detail(@RequestParam Integer id) {
        return ResponseResult.getSuccess(bmsProductStockInService.detail(id));
    }

}
