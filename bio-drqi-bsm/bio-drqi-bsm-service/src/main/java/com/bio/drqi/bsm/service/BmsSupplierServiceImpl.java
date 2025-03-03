package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsSupplierAddReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierExportExcelReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BmsSupplierServiceImpl implements BmsSupplierService {
    @Override
    public PageInfo<BmsSupplierListPageRspDTO> listPage(BmsSupplierListPageReqDTO bmsSupplierListPageReqDTO) {
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
