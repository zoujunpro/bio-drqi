package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsSynKdListPageReqDTO;
import com.bio.drqi.bsm.req.BmsSynKdExecuteReqDTO;
import com.bio.drqi.bsm.rsp.BmsSynKdListPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface BmsSynKdService {

    PageInfo<BmsSynKdListPageRspDTO> listPage(BmsSynKdListPageReqDTO bmsSynKdListPageReqDTO);


    void execute(BmsSynKdExecuteReqDTO bmsSynKdExecuteReqDTO);

    String findLastSuccessTime();
}
