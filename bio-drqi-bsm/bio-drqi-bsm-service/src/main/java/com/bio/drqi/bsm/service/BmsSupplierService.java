package com.bio.drqi.bsm.service;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsSupplierAddReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierEditReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierExportExcelReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsBrandDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierQueryByBrandCodeRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


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

    BmsSupplierQueryByBrandCodeRspDTO queryByBrandCode(String brandCode);


    /**
     * 供应商管理-新增
     *
     * @param bmsSupplierAddReqDTO
     * @return
     */

    void add(BmsSupplierAddReqDTO bmsSupplierAddReqDTO);


    /**
     * 供应商管理-编辑
     *
     * @param bmsSupplierEditReqDTO
     * @return
     */
    void edit(BmsSupplierEditReqDTO bmsSupplierEditReqDTO);

    /**
     * 供应商管理-详情
     *
     * @return
     */
    BmsBrandDetailRspDTO detail(Integer id);

    /**
     * 供应商管理-删除
     *
     * @param id
     * @return
     */
    void delete(Integer id);

    /**
     * 供应商管理-移出回收站供应商
     *
     * @param id
     * @return
     */
    void move(Integer id);

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
