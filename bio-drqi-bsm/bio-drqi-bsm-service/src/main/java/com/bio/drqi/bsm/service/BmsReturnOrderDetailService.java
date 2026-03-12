package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsReturnOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailQueryByOrderDetailNumRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface BmsReturnOrderDetailService {

    PageInfo<BmsReturnOrderDetailListPageRspDTO> listPage(BmsReturnOrderDetailListPageReqDTO bmsReturnOrderDetailListPageReqDTO);

    List<BmsReturnOrderDetailQueryByOrderDetailNumRspDTO> queryByOrderDetailNum(String orderDetailNum);

    void exportExcel( BmsReturnOrderDetailListPageReqDTO bmsReturnOrderDetailListPageReqDTO, HttpServletResponse httpServletResponse);
}
