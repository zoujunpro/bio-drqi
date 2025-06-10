package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.uuid.IdUtils;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.*;
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
    @RequirePermissions("bms:order:listPage")
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
    @RequirePermissions("bms:order:detail")
    public ResponseResult<BmsOrderDetailRspDTO> detail(@RequestParam Integer id) {
        return ResponseResult.getSuccess(bmsOrderService.detail(id));
    }

    /**
     * 上传合同
     * @return
     */
    @WebLog(desc = "采购订单管理-上传合同")
    @PostMapping("/uploadContract")
    @RequirePermissions("bms:order:edit")
    public ResponseResult<String> uploadContract(@RequestBody BmsOrderUploadContractReqDTO bmsOrderUploadContractReqDTO){
        bmsOrderService.uploadContract(bmsOrderUploadContractReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 上传发票
     * @return
     */
    @PostMapping("/uploadInvoice")
    @WebLog(desc = "采购订单管理-上传发票")
    @RequirePermissions("bms:order:edit")
    public ResponseResult<String> uploadInvoice(@RequestBody  BmsOrderUploadInvoiceReqDTO bmsOrderUploadInvoiceReqDTO){
        bmsOrderService.uploadInvoice(bmsOrderUploadInvoiceReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 订单报账结算
     * @return
     */

    @WebLog(desc = "采购订单管理-订单报账结算")
    @PostMapping("/reportAccount")
    @RequirePermissions("bms:order:edit")
    public ResponseResult<String> reportAccount(@RequestBody BmsOrderReportAccountReqDTO bmsOrderReportAccountReqDTO){
        bmsOrderService.reportAccount(bmsOrderReportAccountReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 上传结算凭证
     * @return
     */

    @WebLog(desc = "采购订单管理-上传结算凭证")
    @PostMapping("/uploadPaymentVoucher")
    @RequirePermissions("bms:order:edit")
    public ResponseResult<String> uploadPaymentVoucher(@RequestBody BmsOrderUploadPaymentVoucherReqDTO bmsOrderUploadPaymentVoucherReqDTO){
        bmsOrderService.uploadPaymentVoucher(bmsOrderUploadPaymentVoucherReqDTO);
        return ResponseResult.getSuccess("ok");
    }


}
