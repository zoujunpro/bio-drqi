package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentListDetailRspDTO;
import com.bio.drqi.tc.rsp.TcExperimentListPageRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface TcExperimentService {

    /**
     * 试验方案申请管理-分页查询
     *
     * @param tcExperimentListPageReqDTO
     * @return
     */
    PageInfo<TcExperimentListPageRspDTO> listPage(TcExperimentListPageReqDTO tcExperimentListPageReqDTO);




    /**
     * 试验方案申请管理-田间设计列表
     * @param experimentCode
     * @return
     */
    List<TcExperimentListDetailRspDTO> listDetail( String experimentCode);
}
