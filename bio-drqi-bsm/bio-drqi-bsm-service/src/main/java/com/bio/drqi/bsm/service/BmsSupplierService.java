package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsSupplierAddReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierExportExcelReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListPageRspDTO;
import com.github.pagehelper.PageInfo;


import java.util.List;

public interface BmsSupplierService {

    /**
     * 供应商管理-分页查询
     *
     * @param bmsSupplierListPageReqDTO
     * @return
     */
    PageInfo<BmsSupplierListPageRspDTO> listPage(BmsSupplierListPageReqDTO bmsSupplierListPageReqDTO);

    /**
     * 供应商管理-查询全部
     *
     * @return
     */
    List<BmsSupplierListAllRspDTO> listALl();

    /**
     * 供应商管理-新增
     *
     * @param bmsSupplierAddReqDTO
     * @return
     */

    void add(BmsSupplierAddReqDTO bmsSupplierAddReqDTO);

    /**
     * 供应商管理-删除
     *
     * @param id
     * @return
     */
    void delete(Integer id);

    /**
     * 供应商管理-导出
     *
     * @return
     */
    void exportExcel(BmsSupplierExportExcelReqDTO bmsSupplierExportExcelReqDTO);

    /**
     * 供应商管理-导入
     *
     * @return
     */
    void importExcel();
}
