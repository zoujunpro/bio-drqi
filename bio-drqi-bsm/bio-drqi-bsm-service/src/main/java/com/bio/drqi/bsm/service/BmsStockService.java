package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsStockAddReqDTO;
import com.bio.drqi.bsm.req.BmsStockEditReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockQueryByUnitRspDTO;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface BmsStockService {
    List<BmsStockQueryByUnitRspDTO> queryStockByUnit(String unitCode);


    void add(BmsStockAddReqDTO bmsStockAddReqDTO);

    void edit(BmsStockEditReqDTO bmsStockEditReqDTO);

    void delete(Integer id);


    void downJieCunStockExcel(String dateTime, HttpServletResponse httpServletResponse);

}
