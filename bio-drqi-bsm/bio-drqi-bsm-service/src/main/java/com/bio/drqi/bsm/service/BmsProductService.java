package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsProductListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductQueryListRspDTO;
import com.bio.drqi.domain.BmsProductTb;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface BmsProductService {


    /**
     * 商品管理-分页查询
     *
     * @param bmsProductListPageReqDTO
     * @return
     */
    PageInfo<BmsProductListPageRspDTO> listPage(BmsProductListPageReqDTO bmsProductListPageReqDTO);

    List<String> listAllProductName();

    List<BmsProductListAllRspDTO> listAll();

    /**
     * 商品管理-查询
     *
     * @return
     */
    List<BmsProductQueryListRspDTO> queryList(BmsProductQueryListReqDTO bmsProductQueryListReqDTO);

    /**
     * 商品管理-导出全部
     *
     * @return
     */
    void exportExcel(BmsProductExportExcelReqDTO bmsProductExportExcelReqDTO);

    void exportUnsyncedExcel(BmsProductUnsyncedExportReqDTO reqDTO, HttpServletResponse httpServletResponse);


    /**
     * 商品管理-添加
     *
     * @return
     */
    BmsProductTb add(BmsProductAddReqDTO bmsProductAddReqDTO);


    /**
     * 商品管理-禁用
     *
     * @return
     */
    void disable(Integer id);

    /**
     * 商品管理-启用
     *
     * @return
     */
    void enable(Integer id);

    /**
     * 商品管理-编辑
     *
     * @return
     */
    void edit(BmsProductEditReqDTO bmsProductEditReqDTO);

    void modifyPurchaseTypeCode(BmsProductModifyPurchaseTypeCodeReqDTO bmsProductModifyPurchaseTypeCodeReqDTO);
}
