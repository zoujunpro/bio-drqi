package com.bio.drqi.bsm.service;


import com.bio.drqi.bsm.req.BmsOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailQueryByOrderNumRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BmsOrderDetailService {

    PageInfo<BmsOrderDetailListPageRspDTO> listPage( BmsOrderDetailListPageReqDTO bmsOrderDetailListPageReqDTO);

    List<BmsOrderDetailQueryByOrderNumRspDTO> queryByOrderNum(String orderNum);
}
