package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.TcExperimentApplyListPageReqDTO;
import com.bio.drqi.tc.req.TcExperimentQueryByPdAndVectorTaskCodeReqDTO;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.*;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface TcExperimentApplyService {

    /**
     * 试验方案申请管理-分页查询
     *
     * @param tcExperimentApplyListPageReqDTO
     * @return
     */
    PageInfo<TcExperimentApplyListPageRspDTO> listPage(TcExperimentApplyListPageReqDTO tcExperimentApplyListPageReqDTO);
    List<TcExperimentApplyQueryByPdAndVectorTaskCodeRspDTO> queryByPdAndVectorTaskCode(TcExperimentQueryByPdAndVectorTaskCodeReqDTO tcExperimentQueryByPdAndVectorTaskCodeReqDTO);

    List<TcExperimentApplyListAllRspDTO> listAll();

    void downTemplate(HttpServletResponse httpServletResponse);

    /**
     * 试验方案申请管理-田间设计列表
     *
     * @param experimentNum
     * @return
     */
    List<TcExperimentListDetailRspDTO> listDetail(String experimentNum);

    void complete(Integer id);

    void stop(Integer id);

    void start(Integer id);

}
