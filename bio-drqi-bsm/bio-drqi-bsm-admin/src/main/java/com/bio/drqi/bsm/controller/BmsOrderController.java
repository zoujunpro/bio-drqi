package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsOrderListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsOrderListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderListPageRspDTO;
import com.bio.drqi.bsm.service.BmsOrderService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 采购订单管理-查询全部
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "采购订单管理-查询全部")
    public ResponseResult<List<BmsOrderListAllRspDTO>> listALl() {
        return ResponseResult.getSuccess(bmsOrderService.listALl());
    }

}
