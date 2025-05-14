package com.bio.drqi.tc.service;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageRspDTO;
import com.github.pagehelper.PageInfo;
import javax.servlet.http.HttpServletResponse;

public interface TcPollinationService {

    /**
     * 授粉管理-分页查询
     *
     * @return
     */
    PageInfo<TcPollinationListPageRspDTO> listPage(TcPollinationListPageReqDTO tcPollinationListPageReqDTO);

    /**
     * 授粉管理-授粉列表分页查询
     *
     * @return
     */
    PageInfo<TcPollinationListPageDetailRspDTO> listPageDetail(TcPollinationListPageDetailReqDTO tcPollinationListPageDetailReqDTO);


    /**
     * 授粉管理-生成授粉excel
     */
    void createPollinationExcel(TcPollinationCreatePollinationExcelReqDTO tcPollinationCreatePollinationExcelReqDTO, HttpServletResponse httpServletResponse);

}
