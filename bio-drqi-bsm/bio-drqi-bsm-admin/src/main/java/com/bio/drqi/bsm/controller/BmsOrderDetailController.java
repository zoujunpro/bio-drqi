package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.rsp.BmsOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailQueryByOrderNumRspDTO;
import com.bio.drqi.bsm.service.BmsOrderDetailService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 采购订单明细管理
 */
@RestController
@RequestMapping("/orderDetail")
public class BmsOrderDetailController {

    @Resource
    private BmsOrderDetailService bmsOrderDetailService;


    @PostMapping("listPage")
    @WebLog(desc = "采购订单明细管理-分页查询")
    public ResponseResult<PageInfo<BmsOrderDetailListPageRspDTO>> listPage(@RequestBody BmsOrderDetailListPageRspDTO bmsOrderDetailListPageRspDTO) {
        return null;
    }


    /**
     * 采购订单明细管理-订单号查询
     *
     * @return
     */
    @GetMapping("/queryLByOrderNum")
    @WebLog(desc = "采购订单明细管理-订单号查询")
    public ResponseResult<List<BmsOrderDetailQueryByOrderNumRspDTO>> queryByOrderNum(@RequestParam String orderNum) {

        return ResponseResult.getSuccess(bmsOrderDetailService.queryByOrderNum(orderNum));
    }
}
