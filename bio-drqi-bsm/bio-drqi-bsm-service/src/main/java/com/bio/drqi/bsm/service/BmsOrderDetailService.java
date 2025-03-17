package com.bio.drqi.bsm.service;


import com.bio.drqi.bsm.rsp.BmsOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailQueryByOrderNumRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BmsOrderDetailService {

    PageInfo<BmsOrderDetailListPageRspDTO> listPage( BmsOrderDetailListPageRspDTO bmsOrderDetailListPageRspDTO);

    List<BmsOrderDetailQueryByOrderNumRspDTO> queryByOrderNum(String orderNum);
}
