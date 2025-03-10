package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsProductTypeAddReqDTO;
import com.bio.drqi.bsm.req.BmsProductTypeEditReqDTO;
import com.bio.drqi.bsm.req.BmsProductTypeListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListPageRspDTO;
import com.github.pagehelper.PageInfo;


import java.util.List;

public interface BmsProductTypeService {

    PageInfo<BmsProductTyListPageRspDTO> listPage(BmsProductTypeListPageReqDTO bmsProductTypeListPageReqDTO);

    List<BmsProductTyListAllRspDTO> listAll();

    void add(BmsProductTypeAddReqDTO bmsProductTypeAddReqDTO);

    void delete(Integer id);

    void edit(BmsProductTypeEditReqDTO bmsProductTypeEditReqDTO);
}
