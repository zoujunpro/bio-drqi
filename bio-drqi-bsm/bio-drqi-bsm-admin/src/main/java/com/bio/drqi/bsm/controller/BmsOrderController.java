package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.bsm.req.BmsOrderListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsOrderListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 采购订单管理
 */
@RestController
@RequestMapping("/order")
public class BmsOrderController {

    public ResponseResult<PageInfo<BmsOrderListPageRspDTO>> listPage(@RequestBody @Validated BmsOrderListPageReqDTO bmsOrderListPageReqDTO) {
        return null;
    }

    public ResponseResult<List<BmsOrderListAllRspDTO>> listALl() {
        return null;
    }

}
