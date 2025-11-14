package com.bio.drqi.plant.service;

import com.bio.drqi.plant.req.PlantExperimentReqDTO;
import com.bio.drqi.plant.rsp.PlantExperimentRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;

public interface PlantExperimentService {

    /**
     * CER试验管理-分页查询
     *
     * @param plantExperimentReqDTO
     * @return
     */
    PageInfo<PlantExperimentRspDTO> listPage(@RequestBody PlantExperimentReqDTO plantExperimentReqDTO);

    /**
     * CER试验管理-下载模板
     *
     * @param httpServletResponse
     */
    void downloadTemplate(HttpServletResponse httpServletResponse);
}
