package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;

public interface TcHarvestService {

    PageInfo<TcHarvestListPageRspDTO> listPage(@RequestBody TcHarvestListPageReqDTO tcHarvestListPageReqDTO);

    PageInfo<TcHarvestListPageDetailRspDTO> listPageDetail(TcHarvestListPageDetailReqDTO tcHarvestListPageDetailReqDTO);

    void createHarvestExcel(TcHarvestCreateHarvestExcelReqDTO tcPollinationCreatePollinationExcelReqDTO, HttpServletResponse httpServletResponse);

}
