package com.bio.drqi.bsm.service;


import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailQueryByOrderNumRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDtlDetailRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface BmsOrderDetailService {

    PageInfo<BmsOrderDetailListPageRspDTO> listPage(BmsOrderDetailListPageReqDTO bmsOrderDetailListPageReqDTO);

    BmsOrderDtlDetailRspDTO detail(Integer id);

    List<BmsOrderDetailQueryByOrderNumRspDTO> queryByOrderNum(String orderNum);


    /**
     * 上传合同
     *
     * @return
     */
    void uploadContract(BmsOrderDetailUploadContractReqDTO bmsOrderDetailUploadContractReqDTO);

    /**
     * 删除合同
     * @param bmsOrderDetailDeleteContractReqDTO
     */
    void deleteContract( BmsOrderDetailDeleteContractReqDTO bmsOrderDetailDeleteContractReqDTO);

    /**
     * 上传发票
     *
     * @return
     */
    void uploadInvoice(BmsOrderDetailUploadInvoiceReqDTO bmsOrderDetailUploadInvoiceReqDTO);

    /**
     * 订单报账结算
     *
     * @return
     */

    void reportAccount(BmsOrderDetailReportAccountReqDTO bmsOrderDetailReportAccountReqDTO);

    void uploadPaymentVoucher(BmsOrderDetailUploadPaymentVoucherReqDTO bmsOrderDetailUploadPaymentVoucherReqDTO);

    void taxRate(BmsOrderDetailTaxRateReqDTO bmsOrderDetailTaxRateReqDTO);


    void exportExcel(BmsOrderDetailExportExcelReqDTO bmsOrderDetailExportExcelReqDTO, HttpServletResponse httpServletResponse);

    void modifyPrice( BmsOrderDetailModifyPriceReqDTO bmsOrderDetailModifyPriceReqDTO);
}
