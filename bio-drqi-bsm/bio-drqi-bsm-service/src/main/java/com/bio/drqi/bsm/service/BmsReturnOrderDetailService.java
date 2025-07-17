package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsReturnOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailQueryByOrderDetailNumRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BmsReturnOrderDetailService {

    PageInfo<BmsReturnOrderDetailListPageRspDTO> listPage(BmsReturnOrderDetailListPageReqDTO bmsReturnOrderDetailListPageReqDTO);

    List<BmsReturnOrderDetailQueryByOrderDetailNumRspDTO> queryByOrderDetailNum(String orderDetailNum);
}
