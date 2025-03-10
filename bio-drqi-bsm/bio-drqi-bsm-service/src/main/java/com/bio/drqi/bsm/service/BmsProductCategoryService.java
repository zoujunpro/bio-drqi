package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsProductCategoryAddReqDTO;
import com.bio.drqi.bsm.req.BmsProductCategoryEditReqDTO;
import com.bio.drqi.bsm.req.BmsProductCategoryListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProductTypeEditReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductCategoryListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductCategoryListPageRspDTO;
import com.github.pagehelper.PageInfo;


import java.util.List;

public interface BmsProductCategoryService {


    PageInfo<BmsProductCategoryListPageRspDTO> listPage(BmsProductCategoryListPageReqDTO  bmsProductCategoryListPageReqDTO);

    List<BmsProductCategoryListAllRspDTO> listAll();

    void add(BmsProductCategoryAddReqDTO bmsProductCategoryAddReqDTO);

    void delete(Integer id);

    void edit(BmsProductCategoryEditReqDTO bmsProductCategoryEditReqDTO);
}
