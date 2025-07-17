package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsMoveOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsMoveOrderDetailListPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface BmsMoveOrderDetailService {

    PageInfo<BmsMoveOrderDetailListPageRspDTO> listPage(BmsMoveOrderDetailListPageReqDTO bmsMoveOrderDetailListPageReqDTO);
}
