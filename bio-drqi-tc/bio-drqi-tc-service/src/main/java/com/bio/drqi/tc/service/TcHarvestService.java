package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.TcHarvestCreateHarvestExcelReqDTO;

import javax.servlet.http.HttpServletResponse;

public interface TcHarvestService {

    void createHarvestExcel(TcHarvestCreateHarvestExcelReqDTO tcPollinationCreatePollinationExcelReqDTO, HttpServletResponse httpServletResponse);

}
