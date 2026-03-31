package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.TcPollinationSingleNumListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationSingleNumListPageRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 授粉单株编号管理服务
 */
public interface TcPollinationSingleNumService {

    /**
     * 分页查询授粉单株编号
     *
     * @param tcPollinationSingleNumListPageReqDTO 查询条件
     * @return 分页结果
     */
    PageInfo<TcPollinationSingleNumListPageRspDTO> listPage(TcPollinationSingleNumListPageReqDTO tcPollinationSingleNumListPageReqDTO);

    /**
     * 根据 ID 查询详情
     *
     * @param id 主键 ID
     * @return 详情信息
     */
    TcPollinationSingleNumListPageRspDTO detail(Integer id);

 }
