package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface TcPollinationService {

    /**
     * 授粉管理-分页查询
     *
     * @return
     */
    PageInfo<TcPollinationListPageRspDTO> listPage(TcPollinationListPageReqDTO tcPollinationListPageReqDTO);


    /**
     * 授粉管理-生成授粉excel
     */
    void createPollinationExcel(TcPollinationCreatePollinationExcelReqDTO tcPollinationCreatePollinationExcelReqDTO);

}
