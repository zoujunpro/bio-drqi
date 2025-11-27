package com.bio.drqi.manage.controller.plant;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;

import com.bio.drqi.manage.plant.req.PlantApplyListPageDetailReqDTO;
import com.bio.drqi.manage.plant.req.PlantApplyListPageReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantApplyListPageDetailRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantApplyListPageRspDTO;
import com.bio.drqi.manage.service.plant.PlantApplyService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * CER种植申请管理
 */
@RestController
@RequestMapping("plantApply")
public class PlantApplyController {

    @Resource
    private PlantApplyService plantApplyService;

    /**
     * CER种植申请管理-分页查询
     *
     * @param plantApplyListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "CER种植申请管理-分页查询")
    public ResponseResult<PageInfo<PlantApplyListPageRspDTO>> listPage(@RequestBody PlantApplyListPageReqDTO plantApplyListPageReqDTO) {
        return ResponseResult.getSuccess(plantApplyService.listPage(plantApplyListPageReqDTO));
    }

    /**
     * CER种植申请管理-下载模板
     *
     * @param httpServletResponse
     */
    @GetMapping("/downloadTemplate")
    @WebLog(desc = "CER种植申请管理-下载模板")
    public void downloadTemplate(HttpServletResponse httpServletResponse) {
        plantApplyService.downloadTemplate(httpServletResponse);
    }

    /**
     * CER种植申请管理-分页查询试验详情
     *
     * @param plantApplyListPageDetailReqDTO
     * @return
     */
    @PostMapping("/listPageDetail")
    @WebLog(desc = "CER种植申请管理-分页查询试验详情")
    public ResponseResult<PageInfo<PlantApplyListPageDetailRspDTO>> listPageDetail(@RequestBody PlantApplyListPageDetailReqDTO plantApplyListPageDetailReqDTO) {
        return ResponseResult.getSuccess(plantApplyService.listPageDetail(plantApplyListPageDetailReqDTO));
    }

}
