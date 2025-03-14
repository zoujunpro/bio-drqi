package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsOrderListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsOrderListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderListPageRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BmsOrderService {

    PageInfo<BmsOrderListPageRspDTO> listPage(BmsOrderListPageReqDTO bmsOrderListPageReqDTO);

    List<BmsOrderListAllRspDTO> listALl();
}
