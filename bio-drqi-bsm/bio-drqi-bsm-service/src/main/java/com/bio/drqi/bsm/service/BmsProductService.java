package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsProductListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductQueryListRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BmsProductService {


    /**
     * 商品管理-分页查询
     * @param bmsProductListPageReqDTO
     * @return
     */
    PageInfo<BmsProductListPageRspDTO> listPage(BmsProductListPageReqDTO bmsProductListPageReqDTO);

    /**
     * 商品管理-查询
     * @return
     */
    List<BmsProductQueryListRspDTO> queryList(BmsProductQueryListReqDTO bmsProductQueryListReqDTO);

    /**
     * 商品管理-导出全部
     * @return
     */
     void exportExcel(BmsProductExportExcelReqDTO bmsProductExportExcelReqDTO) ;


    /**
     * 商品管理-添加
     * @return
     */
    void add(BmsProductAddReqDTO bmsProductAddReqDTO);


    /**
     * 商品管理-删除
     * @return
     */
    void delete( Integer id);

    /**
     * 商品管理-编辑
     * @return
     */
    void edit(BmsProductEditReqDTO bmsProductEditReqDTO);
}
