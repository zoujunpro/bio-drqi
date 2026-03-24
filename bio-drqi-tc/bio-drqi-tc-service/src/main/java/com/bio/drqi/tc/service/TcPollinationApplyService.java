package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationApplyExportPollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcPollinationApplyListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationApplyListPageRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationApplyListPollinationApplyNumNotHarvestRspDTO;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface TcPollinationApplyService {

    /**
     * 授粉管理-分页查询
     *
     * @return
     */
    PageInfo<TcPollinationApplyListPageRspDTO> listPage(TcPollinationApplyListPageReqDTO tcPollinationApplyListPageReqDTO);

    List<TcPollinationApplyListPollinationApplyNumNotHarvestRspDTO> listPollinationApplyNumNotHarvest();


    /**
     * 授粉管理-生成授粉excel
     */
    List<TcPollinationExcelDTO> createPollinationExcel(TcPollinationCreatePollinationExcelReqDTO tcPollinationCreatePollinationExcelReqDTO );


  void   exportPollinationExcel(TcPollinationApplyExportPollinationExcelReqDTO tcPollinationApplyExportPollinationExcelReqDTO, HttpServletResponse httpServletResponse);


}
