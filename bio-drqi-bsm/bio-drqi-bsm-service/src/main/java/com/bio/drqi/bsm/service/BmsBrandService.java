package com.bio.drqi.bsm.service;
import com.bio.drqi.bsm.req.BmsBrandAddReqDTO;
import com.bio.drqi.bsm.req.BmsBrandEditReqDTO;
import com.bio.drqi.bsm.req.BmsBrandListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsBrandListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsBrandListPageRspDTO;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface BmsBrandService {

    /**
     * 品牌管理-分页查询
     *
     * @param bmsBrandListPageReqDTO
     * @return
     */
    PageInfo<BmsBrandListPageRspDTO> listPage(BmsBrandListPageReqDTO bmsBrandListPageReqDTO);


    /**
     * 品牌管理-查询所有
     *
     * @return
     */
    List<BmsBrandListAllRspDTO> listAll();

    /**
     * 品牌管理-新增
     *
     * @return
     */
    void add(BmsBrandAddReqDTO bmsBrandAddReqDTO);

    /**
     * 品牌管理-删除
     *
     * @return
     */
    void delete(Integer id);

    /**
     * 品牌管理-修改
     *
     * @return
     */
    void edit(BmsBrandEditReqDTO bmsBrandEditReqDTO);
}
