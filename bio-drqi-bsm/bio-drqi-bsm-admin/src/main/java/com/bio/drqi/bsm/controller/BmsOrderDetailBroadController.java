package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsStockBroadCountOrderReqDTO;
import com.bio.drqi.bsm.rsp.*;
import com.bio.drqi.bsm.service.BmsOrderDetailBroadService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
    @WebLog(desc = "采购统计-总金额统计")
    public ResponseResult<BmsOrderDetailBroadOrderCountRspDTO> orderCount(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        return ResponseResult.getSuccess(bmsOrderDetailBroadService.orderCount(bmsStockBroadCountOrderReqDTO));
    }
    /**
     * 采购统计-按照类别统计报账金额/采购金额
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    @PostMapping("/countAmountByByCategory")
    @WebLog(desc = "采购统计-按照类别统计报账金额/采购金额")
    public ResponseResult<List<BmsOrderBroadCountByCategoryRspDTO>> countAmountByByCategory(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        return ResponseResult.getSuccess(bmsOrderDetailBroadService.countAmountByByCategory(bmsStockBroadCountOrderReqDTO));
    }

    /**
     * 采购统计-按日期统计采购/报账金额
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    @PostMapping("/directionAmountCount")
    @WebLog(desc = "采购统计-按日期统计采购金额")
    public ResponseResult<List<BmsOrderDetailDirectionAmountCountCountRspDTO>> directionAmountCount(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        return ResponseResult.getSuccess(bmsOrderDetailBroadService.directionAmountCount(bmsStockBroadCountOrderReqDTO));
    }

    /**
     * 采购统计-按供应商统计采购金额（前10位）
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    @PostMapping("/directionSupplierCount")
    @WebLog(desc = "采购统计-按供应商统计采购金额（前10位）")
    public ResponseResult<List<BmsOrderDetailDirectionSupplierCountCountRspDTO>> directionSupplierCount(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        return ResponseResult.getSuccess(bmsOrderDetailBroadService.directionSupplierCount(bmsStockBroadCountOrderReqDTO));
    }


    /**
     * 采购统计-已入库未报账数据
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    @PostMapping("/queryReportNoInStockListPage")
    @WebLog(desc = "采购统计-已入库未报账数据")
    public ResponseResult<PageInfo<BmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO>> queryReportNoInStockListPage(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        return ResponseResult.getSuccess(bmsOrderDetailBroadService.queryReportNoInStockListPage(bmsStockBroadCountOrderReqDTO));
    }

    /**
     * 采购统计-导出已入库未报账数据
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    @PostMapping("/exportReportNoInStockListPage")
    @WebLog(desc = "采购统计-导出已入库未报账数据")
    public void exportReportNoInStockListPage(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO, HttpServletResponse httpServletResponse) {
        bmsOrderDetailBroadService.exportReportNoInStockListPage(bmsStockBroadCountOrderReqDTO, httpServletResponse);
    }

}
