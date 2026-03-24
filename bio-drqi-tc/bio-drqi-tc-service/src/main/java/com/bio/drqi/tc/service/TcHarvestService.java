package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.TcHarvestApplyListPageReqDTO;
import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;
import com.bio.drqi.tc.req.TcHarvestListPageDetailReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestApplyListPageRspDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageDetailRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;

public interface TcHarvestService {


    PageInfo<TcHarvestListPageDetailRspDTO> listPage(TcHarvestListPageDetailReqDTO tcHarvestListPageDetailReqDTO);


}
