package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.TcExperimentApplyListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentApplyListPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface TcExperimentApplyService {

    /**
     * 试验方案申请管理-分页查询
     *
     * @param tcExperimentApplyListPageReqDTO
     * @return
     */
    PageInfo<TcExperimentApplyListPageRspDTO> listPage(TcExperimentApplyListPageReqDTO tcExperimentApplyListPageReqDTO);
}
