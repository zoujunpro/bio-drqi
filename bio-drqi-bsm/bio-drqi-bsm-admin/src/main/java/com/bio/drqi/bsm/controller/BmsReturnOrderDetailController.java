package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsProductStockInLogReturnStockReqDTO;
import com.bio.drqi.bsm.req.BmsReturnOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailQueryByOrderDetailNumRspDTO;
import com.bio.drqi.bsm.service.BmsReturnOrderDetailService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 退回订单管理
 */
@RestController
@RequestMapping("/returnOrderDetail")
public class BmsReturnOrderDetailController {

    @Resource
    private BmsReturnOrderDetailService bmsReturnOrderDetailService;

    /**
     * 退回订单管理-分页查询
     *
     * @param bmsReturnOrderDetailListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "退回订单管理-分页查询")
    @RequirePermissions("bms:returnOrderDetail:listPage")
    public ResponseResult<PageInfo<BmsReturnOrderDetailListPageRspDTO>> listPage(@RequestBody BmsReturnOrderDetailListPageReqDTO bmsReturnOrderDetailListPageReqDTO) {
        return ResponseResult.getSuccess(bmsReturnOrderDetailService.listPage(bmsReturnOrderDetailListPageReqDTO));
    }


    /**
     * 退回订单管理-更加订单明细查询
     *
     * @param orderDetailNum
     * @return
     */
    @GetMapping("/queryByOrderDetailNum")
    @WebLog(desc = "退回订单管理-更加订单明细查询")
    public ResponseResult<List<BmsReturnOrderDetailQueryByOrderDetailNumRspDTO>> queryByOrderDetailNum(@RequestParam @Validated @NotBlank(message = "订单明细入参缺失") String orderDetailNum) {
        return ResponseResult.getSuccess(bmsReturnOrderDetailService.queryByOrderDetailNum(orderDetailNum));
    }
}
