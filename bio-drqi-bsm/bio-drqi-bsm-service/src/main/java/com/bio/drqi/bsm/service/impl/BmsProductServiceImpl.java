package com.bio.drqi.bsm.service.impl;

import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsProductListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductService;
import com.bio.drqi.mapper.BmsProductTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BmsProductServiceImpl implements BmsProductService {


    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Override
    public PageInfo<BmsProductListPageRspDTO> listPage(BmsProductListPageReqDTO bmsProductListPageReqDTO) {
        PageHelper.startPage(bmsProductListPageReqDTO.getPageNum(),bmsProductListPageReqDTO.getPageSize());
        return null;
    }

    @Override
    public void list(BmsProductListReqDTO bmsProductListReqDTO) {

    }

    @Override
    public void exportExcel(BmsProductExportExcelReqDTO bmsProductExportExcelReqDTO) {

    }

    @Override
    public void add(BmsProductAddReqDTO bmsProductAddReqDTO) {

    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public void edit(BmsProductEditReqDTO bmsProductEditReqDTO) {

    }
}
