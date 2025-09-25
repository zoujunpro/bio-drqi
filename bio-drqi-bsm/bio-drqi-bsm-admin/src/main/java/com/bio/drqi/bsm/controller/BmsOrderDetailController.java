package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailQueryByOrderNumRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDtlDetailRspDTO;
import com.bio.drqi.bsm.service.BmsOrderDetailService;
import com.bio.drqi.common.aspect.RequestLog;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
    @RequirePermissions("bms:orderDetail:listPage")
    public ResponseResult<PageInfo<BmsOrderDetailListPageRspDTO>> listPage(@RequestBody BmsOrderDetailListPageReqDTO bmsOrderDetailListPageReqDTO) {
        return ResponseResult.getSuccess(bmsOrderDetailService.listPage(bmsOrderDetailListPageReqDTO));
    }

    /**
     * 采购订单管理-详情
     * @return
     */
    @GetMapping("/detail")
    @WebLog(desc = "采购订单明细管理-详情")
    @RequirePermissions("bms:orderDetail:detail")
    public ResponseResult<BmsOrderDtlDetailRspDTO> detail(@RequestParam Integer id) {
        return ResponseResult.getSuccess(bmsOrderDetailService.detail(id));
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


    /**
     * 上传合同
     * @return
     */
    @WebLog(desc = "采购订单管理-上传合同")
    @PostMapping("/uploadContract")
    @RequirePermissions("bms:orderDetail:edit")
    @RequestLog("采购订单管理-上传合同")
    public ResponseResult<String> uploadContract(@RequestBody BmsOrderDetailUploadContractReqDTO bmsOrderDetailUploadContractReqDTO){
        bmsOrderDetailService.uploadContract(bmsOrderDetailUploadContractReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 上传发票
     * @return
     */
    @PostMapping("/uploadInvoice")
    @WebLog(desc = "采购订单管理-上传发票")
    @RequirePermissions("bms:orderDetail:edit")
    @RequestLog("采购订单管理-上传发票")
    public ResponseResult<String> uploadInvoice(@RequestBody BmsOrderDetailUploadInvoiceReqDTO bmsOrderDetailUploadInvoiceReqDTO){
        bmsOrderDetailService.uploadInvoice(bmsOrderDetailUploadInvoiceReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 订单报账结算
     * @return
     */

    @WebLog(desc = "采购订单管理-订单报账结算")
    @PostMapping("/reportAccount")
    @RequirePermissions("bms:orderDetail:edit")
    @RequestLog("采购订单管理-订单报账结算")
    public ResponseResult<String> reportAccount(@RequestBody @Validated BmsOrderDetailReportAccountReqDTO bmsOrderDetailReportAccountReqDTO){
        bmsOrderDetailService.reportAccount(bmsOrderDetailReportAccountReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 上传结算凭证
     * @return
     */

    @WebLog(desc = "采购订单管理-上传结算凭证")
    @PostMapping("/uploadPaymentVoucher")
    @RequirePermissions("bms:orderDetail:edit")
    @RequestLog("采购订单管理-上传结算凭证")
    public ResponseResult<String> uploadPaymentVoucher(@RequestBody @Validated BmsOrderDetailUploadPaymentVoucherReqDTO bmsOrderDetailUploadPaymentVoucherReqDTO){
        bmsOrderDetailService.uploadPaymentVoucher(bmsOrderDetailUploadPaymentVoucherReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 采购订单管理-税率
     * @return
     */

    @WebLog(desc = "采购订单管理-税率")
    @PostMapping("/taxRate")
    @RequirePermissions("bms:orderDetail:edit")
    @RequestLog("采购订单管理-税率")
    public ResponseResult<String> taxRate(@RequestBody @Validated BmsOrderDetailTaxRateReqDTO bmsOrderDetailTaxRateReqDTO){
        bmsOrderDetailService.taxRate(bmsOrderDetailTaxRateReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * -采购订单管理导出工单信息
     */
    @WebLog(desc = "采购订单管理-导出")
    @PostMapping("/exportExcel")
    @RequirePermissions("bms:orderDetail:exportExcel")
    @RequestLog("采购订单管理-导出")
    public void exportExcel(@RequestBody @Validated BmsOrderDetailExportExcelReqDTO bmsOrderDetailExportExcelReqDTO, HttpServletResponse httpServletResponse){
        bmsOrderDetailService.exportExcel(bmsOrderDetailExportExcelReqDTO,httpServletResponse);
    }

    /**
     * 采购订单管理-修改采购金额
     */
    @WebLog(desc = "采购订单管理-修改采购金额")
    @PostMapping("/modifyPriceAndNumber")
    @RequirePermissions("bms:orderDetail:modifyPriceAndNumber")
    @RequestLog("采购订单管理-修改采购金额")
    public ResponseResult<String> modifyPrice(@RequestBody @Validated BmsOrderDetailModifyPriceReqDTO bmsOrderDetailModifyPriceReqDTO){
        bmsOrderDetailService.modifyPrice(bmsOrderDetailModifyPriceReqDTO);
        return ResponseResult.getSuccess("修改采购金额和数量成功");
    }
}
