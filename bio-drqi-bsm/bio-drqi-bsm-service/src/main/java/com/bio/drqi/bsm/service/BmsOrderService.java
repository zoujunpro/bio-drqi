package com.bio.drqi.bsm.service;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsOrderDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderQueryListRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BmsOrderService {

    PageInfo<BmsOrderListPageRspDTO> listPage(BmsOrderListPageReqDTO bmsOrderListPageReqDTO);

    List<BmsOrderQueryListRspDTO> queryList( BmsOrderQueryListReqDTO bmsOrderQueryListReqDTO);

    BmsOrderDetailRspDTO detail( Integer id);



    /**
     * 上传合同
     * @return
     */
    void uploadContract( BmsOrderUploadContractReqDTO bmsOrderUploadContractReqDTO);

    /**
     * 上传发票
     * @return
     */
    void uploadInvoice( BmsOrderUploadInvoiceReqDTO bmsOrderUploadInvoiceReqDTO);

    /**
     * 订单报账结算
     * @return
     */

    void reportAccount(BmsOrderReportAccountReqDTO bmsOrderReportAccountReqDTO);
}
