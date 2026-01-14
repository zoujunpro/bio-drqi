package com.bio.drqi.bsm.service;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsStockBroadCountOrderReqDTO;
import com.bio.drqi.bsm.rsp.*;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface BmsOrderDetailBroadService {

    /**
     * 采购统计-总金额统计
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    BmsOrderDetailBroadOrderCountRspDTO orderCount(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO);


    /**
     * 采购统计-按照类别统计报账金额
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    List<BmsOrderBroadCountByCategoryRspDTO> countAmountByByCategory(@RequestBody @Validated BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO);

    /**
     * 采购统计-按日期统计采购金额
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    List<BmsOrderDetailDirectionAmountCountCountRspDTO> directionAmountCount(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO);

    /**
     * 采购统计-按供应商统计采购金额（前10位）
     *
     * @param bmsStockBroadCountOrderReqDTO
     * @return
     */
    List<BmsOrderDetailDirectionSupplierCountCountRspDTO> directionSupplierCount(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO);


    PageInfo<BmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO>  queryReportNoInStockListPage( BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO);

    void exportReportNoInStockListPage(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO, HttpServletResponse httpServletResponse);
}
