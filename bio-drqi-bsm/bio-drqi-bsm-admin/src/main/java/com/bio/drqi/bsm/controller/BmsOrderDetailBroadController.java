package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.bsm.req.BmsStockBroadCountOrderReqDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailBroadOrderCountRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailDirectionAmountCountCountRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailDirectionSupplierCountCountRspDTO;
import com.bio.drqi.bsm.service.BmsOrderDetailBroadService;
import com.bio.drqi.common.contents.BioDrQiContents;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 采购统计
 */
@RestController
@RequestMapping("bmsOrderDetailBroad")
public class BmsOrderDetailBroadController {

    @Resource
    private BmsOrderDetailBroadService bmsOrderDetailBroadService;

    /**
     * 采购统计-总金额统计
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    @PostMapping("/orderCount")
    public ResponseResult<BmsOrderDetailBroadOrderCountRspDTO> orderCount(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        return ResponseResult.getSuccess(bmsOrderDetailBroadService.orderCount(bmsStockBroadCountOrderReqDTO));
    }


    /**
     * 采购统计-按日期统计采购金额
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    @PostMapping("/directionPurchaseAmountCount")
    public ResponseResult<List<BmsOrderDetailDirectionAmountCountCountRspDTO>> directionPurchaseAmountCount(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        return ResponseResult.getSuccess(bmsOrderDetailBroadService.directionAmountCount(bmsStockBroadCountOrderReqDTO, BioDrQiContents.N));
    }

    /**
     * 采购统计-按日期统计报账金额
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    @PostMapping("/directionReportAmountCount")
    public ResponseResult<List<BmsOrderDetailDirectionAmountCountCountRspDTO>> directionReportAmountCount(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        return ResponseResult.getSuccess(bmsOrderDetailBroadService.directionAmountCount(bmsStockBroadCountOrderReqDTO,BioDrQiContents.Y));
    }

    /**
     * 采购统计-按供应商统计采购金额（前10位）
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    @PostMapping("/directionSupplierCount")
    public ResponseResult<List<BmsOrderDetailDirectionSupplierCountCountRspDTO>> directionSupplierCount(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        return ResponseResult.getSuccess(bmsOrderDetailBroadService.directionSupplierCount(bmsStockBroadCountOrderReqDTO));
    }


}
