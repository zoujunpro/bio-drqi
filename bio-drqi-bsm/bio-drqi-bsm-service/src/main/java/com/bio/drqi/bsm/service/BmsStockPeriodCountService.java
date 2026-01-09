package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsStockPeriodCountListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockPeriodCountListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;

public interface BmsStockPeriodCountService {


    PageInfo<BmsStockPeriodCountListPageRspDTO> listPage(@Validated @RequestBody BmsStockPeriodCountListPageReqDTO bmsStockPeriodCountListPageReqDTO);

    void exportExcel(BmsStockPeriodCountListPageReqDTO bmsStockPeriodCountListPageReqDTO, HttpServletResponse httpServletResponse);
}
