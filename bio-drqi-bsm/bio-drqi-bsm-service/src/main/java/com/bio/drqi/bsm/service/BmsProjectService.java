package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsProjectAddReqDTO;
import com.bio.drqi.bsm.req.BmsProjectEditReqDTO;
import com.bio.drqi.bsm.req.BmsProjectListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProjectQueryAllReqDTO;
import com.bio.drqi.bsm.rsp.BmsProjectListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface BmsProjectService {
    /**
     * 耗材管理项目字典表-分页查询
     *
     * @param bmsProjectListPageReqDTO
     * @return
     */
    PageInfo<BmsProjectListPageRspDTO> listPage(@RequestBody BmsProjectListPageReqDTO bmsProjectListPageReqDTO);

    /**
     * 耗材管理项目字典表-查询所有
     *
     * @return
     */
    List<BmsProjectQueryAllReqDTO> queryAll();

    /**
     * 耗材管理项目字典表-新增
     *
     * @param bmsProjectAddReqDTO
     * @return
     */
    void add(BmsProjectAddReqDTO bmsProjectAddReqDTO);


    /**
     * 耗材管理项目字典表-编辑
     *
     * @param bmsProjectEditReqDTO
     * @return
     */
    void edit(BmsProjectEditReqDTO bmsProjectEditReqDTO);

    /**
     * 耗材管理项目字典表-删除
     *
     * @param id
     * @return
     */
    void delete(Integer id);
}
