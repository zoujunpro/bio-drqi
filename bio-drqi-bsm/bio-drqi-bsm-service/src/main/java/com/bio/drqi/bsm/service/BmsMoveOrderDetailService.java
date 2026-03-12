package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsMoveOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsMoveOrderDetailListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;

public interface BmsMoveOrderDetailService {

    PageInfo<BmsMoveOrderDetailListPageRspDTO> listPage(BmsMoveOrderDetailListPageReqDTO bmsMoveOrderDetailListPageReqDTO);

    void exportExcel(BmsMoveOrderDetailListPageReqDTO bmsMoveOrderDetailListPageReqDTO, HttpServletResponse httpServletResponse);
}
