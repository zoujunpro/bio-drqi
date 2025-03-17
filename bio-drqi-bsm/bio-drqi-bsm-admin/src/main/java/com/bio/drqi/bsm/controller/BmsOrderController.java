package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsOrderListPageReqDTO;
import com.bio.drqi.bsm.req.BmsOrderQueryListReqDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderQueryListRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderListPageRspDTO;
import com.bio.drqi.bsm.service.BmsOrderService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 采购订单管理
 */
@RestController
@RequestMapping("/order")
public class BmsOrderController {

    @Resource
    private BmsOrderService bmsOrderService;

    /**
     * 采购订单管理-分页查询
     * @param bmsOrderListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "采购订单管理-分页查询")
    public ResponseResult<PageInfo<BmsOrderListPageRspDTO>> listPage(@RequestBody @Validated BmsOrderListPageReqDTO bmsOrderListPageReqDTO) {
        return ResponseResult.getSuccess(bmsOrderService.listPage(bmsOrderListPageReqDTO));
    }

    /**
     * 采购订单管理-条件查询订单
     * @return
     */
    @PostMapping("/queryList")
    @WebLog(desc = "采购订单管理-条件查询订单")
    public ResponseResult<List<BmsOrderQueryListRspDTO>> queryList(@RequestBody @Validated BmsOrderQueryListReqDTO bmsOrderQueryListReqDTO) {
        return ResponseResult.getSuccess(bmsOrderService.queryList(bmsOrderQueryListReqDTO));
    }
    /**
     * 采购订单管理-详情
     * @return
     */
    @GetMapping("/detail")
    @WebLog(desc = "采购订单管理-详情")
    public ResponseResult<BmsOrderDetailRspDTO> detail(@RequestParam Integer id) {
        return ResponseResult.getSuccess(bmsOrderService.detail(id));
    }

}
