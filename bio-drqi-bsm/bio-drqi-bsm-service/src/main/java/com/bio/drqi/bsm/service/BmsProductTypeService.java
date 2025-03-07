package com.bio.drqi.bsm.service;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.bsm.req.BmsProductTyAddReqDTO;
import com.bio.drqi.bsm.req.BmsProductTyEditReqDTO;
import com.bio.drqi.bsm.req.BmsProductTyListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListPageRspDTO;
import com.github.pagehelper.PageInfo;


import java.util.List;

public interface BmsProductTypeService {

    PageInfo<BmsProductTyListPageRspDTO> listPage(BmsProductTyListPageReqDTO bmsProductTyListPageReqDTO);

    List<BmsProductTyListAllRspDTO> listAll();

    void add(BmsProductTyAddReqDTO bmsProductTyAddReqDTO);

    void delete(Integer id);

    void edit(BmsProductTyEditReqDTO bmsProductTyEditReqDTO);
}
