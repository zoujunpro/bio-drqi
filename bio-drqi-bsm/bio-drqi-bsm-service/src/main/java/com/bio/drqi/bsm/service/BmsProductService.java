package com.bio.drqi.bsm.service;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsProductListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

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
    void list(BmsProductListReqDTO bmsProductListReqDTO);

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
