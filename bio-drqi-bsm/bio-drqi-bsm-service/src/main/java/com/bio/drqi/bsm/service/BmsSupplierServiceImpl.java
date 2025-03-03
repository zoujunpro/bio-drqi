package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsSupplierAddReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierExportExcelReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListPageRspDTO;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.domain.BmsSupplierTb;
import com.bio.drqi.mapper.BmsSupplierTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BmsSupplierServiceImpl implements BmsSupplierService {

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Override
    public PageInfo<BmsSupplierListPageRspDTO> listPage(BmsSupplierListPageReqDTO bmsSupplierListPageReqDTO) {
        PageHelper.startPage(bmsSupplierListPageReqDTO.getPageNum(), bmsSupplierListPageReqDTO.getPageSize());
        List<BmsSupplierTb> bmsSupplierTbList = bmsSupplierTbMapper.selectSelective(BmsSupplierTb.builder().supplierCode(bmsSupplierListPageReqDTO.getSupplierCode()).deleteFlag(BioDrQiContents.Y).supplierName(bmsSupplierListPageReqDTO.getSupplierName()).build());
        return null;
    }

    @Override
    public List<BmsSupplierListAllRspDTO> listALl() {
        return null;
    }

    @Override
    public void add(BmsSupplierAddReqDTO bmsSupplierAddReqDTO) {

    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public void exportExcel(BmsSupplierExportExcelReqDTO bmsSupplierExportExcelReqDTO) {

    }

    @Override
    public void importExcel() {

    }
}
