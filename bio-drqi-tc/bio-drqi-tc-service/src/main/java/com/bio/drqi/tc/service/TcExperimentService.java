package com.bio.drqi.tc.service;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.req.TcExperimentQueryListExperimentDesignReqDTO;
import com.bio.drqi.tc.rsp.*;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface TcExperimentService {

    /**
     * 试验方案申请管理-分页查询
     *
     * @param tcExperimentListPageReqDTO
     * @return
     */
    PageInfo<TcExperimentListPageRspDTO> listPage(TcExperimentListPageReqDTO tcExperimentListPageReqDTO);

    List<TcExperimentListAllRspDTO> listAll();

    List<TcExperimentListNoHarvestRspDTO> listNoHarvest();


    List<TcExperimentQueryListExperimentDesignRspDTO> queryListExperimentDesign( TcExperimentQueryListExperimentDesignReqDTO tcExperimentQueryListExperimentDesignReqDTO);

        /**
         * 试验方案申请管理-田间设计列表
         * @param experimentNum
         * @return
         */
    List<TcExperimentListDetailRspDTO> listDetail( String experimentNum);

}
