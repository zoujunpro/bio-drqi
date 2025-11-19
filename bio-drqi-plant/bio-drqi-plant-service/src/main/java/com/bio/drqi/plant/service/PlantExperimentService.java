package com.bio.drqi.plant.service;

import com.bio.drqi.plant.req.PlantExperimentListPageDetailReqDTO;
import com.bio.drqi.plant.req.PlantExperimentListPageReqDTO;
import com.bio.drqi.plant.rsp.PlantExperimentListPageDetailRspDTO;
import com.bio.drqi.plant.rsp.PlantExperimentListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;

public interface PlantExperimentService {

    /**
     * CER试验管理-分页查询
     *
     * @param plantExperimentListPageReqDTO
     * @return
     */
    PageInfo<PlantExperimentListPageRspDTO> listPage(@RequestBody PlantExperimentListPageReqDTO plantExperimentListPageReqDTO);

    PageInfo<PlantExperimentListPageDetailRspDTO> listPageDetail(@RequestBody PlantExperimentListPageDetailReqDTO plantExperimentListPageDetailReqDTO);

    /**
     * CER试验管理-下载模板
     *
     * @param httpServletResponse
     */
    void downloadTemplate(HttpServletResponse httpServletResponse);
}
