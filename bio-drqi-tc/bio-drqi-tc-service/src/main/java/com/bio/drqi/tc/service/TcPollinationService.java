package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.github.pagehelper.PageInfo;

public interface TcPollinationService {

    /**
     * 授粉详情管理-授粉列表分页查询
     *
     * @return
     */
    PageInfo<TcPollinationListPageDetailRspDTO> listPage(TcPollinationListPageDetailReqDTO tcPollinationListPageDetailReqDTO);


}
