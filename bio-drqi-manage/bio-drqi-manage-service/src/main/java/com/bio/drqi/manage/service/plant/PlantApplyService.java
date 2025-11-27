package com.bio.drqi.manage.service.plant;

import com.bio.drqi.manage.plant.req.PlantApplyListPageDetailReqDTO;
import com.bio.drqi.manage.plant.req.PlantApplyListPageReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantApplyListPageDetailRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantApplyListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;

public interface PlantApplyService {

    /**
     * CER试验管理-分页查询
     *
     * @param plantApplyListPageReqDTO
     * @return
     */
    PageInfo<PlantApplyListPageRspDTO> listPage(@RequestBody PlantApplyListPageReqDTO plantApplyListPageReqDTO);

    PageInfo<PlantApplyListPageDetailRspDTO> listPageDetail(@RequestBody PlantApplyListPageDetailReqDTO plantApplyListPageDetailReqDTO);

    /**
     * CER试验管理-下载模板
     *
     * @param httpServletResponse
     */
    void downloadTemplate(HttpServletResponse httpServletResponse);
}
