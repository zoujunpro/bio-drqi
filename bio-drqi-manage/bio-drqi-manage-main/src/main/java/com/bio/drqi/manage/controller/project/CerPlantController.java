package com.bio.drqi.manage.controller.project;


import com.bio.cer.plant.req.DownloadTemplateReqDTO;
import com.bio.cer.plant.req.PlantListPageReqDTO;
import com.bio.cer.plant.rsp.PlantDetailRspDTO;
import com.bio.cer.plant.rsp.PlantListPageRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.CerPlantService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * CER种植相关信息接口
 * */
@RestController
@RequestMapping("/plant")
public class CerPlantController  {

    @Resource
    private CerPlantService cerPlantService;

    /**
     * 分页查询
     */
    @WebLog(desc = "CER种植分页查询")
    @PostMapping("/listPage")
    @RequirePermissions("project:data:plant")
    public ResponseResult<PageInfo<PlantListPageRspDTO>> listPage(@Validated @RequestBody PlantListPageReqDTO plantListPageReqDTO) {
        PageInfo<PlantListPageRspDTO> pageInfo = cerPlantService.listPage(plantListPageReqDTO);
        return ResponseResult.getSuccess(pageInfo);
    }

    /**
     * 详情查询
     */
    @WebLog(desc = "CER种植详情查询")
    @GetMapping("/detail")
    public ResponseResult<PlantDetailRspDTO> detail(@Validated @RequestParam @NotNull(message = "缺失要查询的种子编号") Integer id) {
        PlantDetailRspDTO plantDetailRspDTO= cerPlantService.detail(id);
        return ResponseResult.getSuccess(plantDetailRspDTO);
    }

    /**
     * CER种植结果信息上传模板下载
     */
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(@RequestBody @Validated DownloadTemplateReqDTO downloadTemplateReqDTO ,HttpServletResponse httpServletResponse) {
        cerPlantService.downloadTemplate(downloadTemplateReqDTO,httpServletResponse);
    }


    /**
     * 物种配置项
     * @return
     */
    @GetMapping("/fieldList")
    public ResponseResult<List<Map<String,String>>> fieldList(@NotBlank @Validated @NotBlank(message = "参数缺失：speciesCode") String speciesCode) {
        return ResponseResult.getSuccess(cerPlantService.fieldList(speciesCode));
    }
}
