package com.bio.drqi.tc.service;


import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentListPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface TcExperimentService {


    PageInfo<TcExperimentListPageRspDTO> listPage(TcExperimentListPageReqDTO tcExperimentListPageReqDTO);
}
