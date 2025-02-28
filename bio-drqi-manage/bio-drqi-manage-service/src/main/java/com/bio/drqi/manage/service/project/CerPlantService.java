package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.plant.req.DownloadTemplateReqDTO;
import com.bio.drqi.manage.plant.req.PlantListPageReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDetailRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantListPageRspDTO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface CerPlantService {

    /**
     * 分页查询
     */
    PageInfo<PlantListPageRspDTO> listPage(PlantListPageReqDTO plantListPageReqDTO);

    /**
     * 详情查询
     */
    PlantDetailRspDTO detail(Integer id);



    /**
     * CER种植结果信息上传模板下载
     */
    void downloadTemplate(DownloadTemplateReqDTO downloadTemplateReqDTO, HttpServletResponse httpServletResponse);

    List<Map<String,String>> fieldList(String speciesCode);
}
