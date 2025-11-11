package com.bio.drqi.manage.controller.project;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.plant.req.PlantDtlListDetailReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlCountRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlListDetailRspDTO;
import com.bio.drqi.manage.sample.req.DownloadSampleTemplateReqDTO;
import com.bio.drqi.manage.sample.req.SampleTestListDetailReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleTestListDetailRspDTO;
import com.bio.drqi.manage.service.project.CerPlantDtlService;
import com.bio.drqi.manage.plant.req.PlantDtlListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlListRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 种植明细
 */
@RestController
@RequestMapping("/plantDtl")
public class CerPlantDtlController {

    @Resource
    private CerPlantDtlService cerPlantDtlService;

    @Resource
    private OssService ossService;


    /**
     * 种植明细-分页查询
     *
     * @param plantDtlListReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "种植明细-分页查询")
    @RequirePermissions("cer:plantDtl:listPage")
    public ResponseResult<PageInfo<PlantDtlListRspDTO>> listPage(@RequestBody PlantDtlListReqDTO plantDtlListReqDTO) {
        return ResponseResult.getSuccess(cerPlantDtlService.listPage(plantDtlListReqDTO));
    }


    /**
     * 种植明细-实施方案下分页查询
     *
     * @param plantDtlListDetailReqDTO
     * @return
     */
    @PostMapping("listDetail")
    @WebLog(desc = "种植明细-实施方案下分页查询")
    public ResponseResult<PageInfo<PlantDtlListDetailRspDTO>> listDetail(@Validated @RequestBody PlantDtlListDetailReqDTO plantDtlListDetailReqDTO) {
        return ResponseResult.getSuccess(cerPlantDtlService.listDetail(plantDtlListDetailReqDTO));
    }


    /**
     * 种植明细-种植模板
     */
    @PostMapping("downSampleTemplate")
    @WebLog(desc = "种植明细-种植模板")
    public void downSampleTemplate(HttpServletResponse response) {
        try {
            ossService.downloadFile(response, "template", "CER种植结果上传数据模板_V1.xlsx");
        } catch (Exception e) {
            throw new BusinessException("CER种植结果上传数据模板下载失败，请联系管理员检测模板配置");
        }
    }
    /**
     * 种植明细-数据统计
     */
    @GetMapping("count")
    @WebLog(desc = "种植明细-数据统计")
    public ResponseResult<PlantDtlCountRspDTO> count(@RequestParam String vectorTaskCode) {
        return ResponseResult.getSuccess(cerPlantDtlService.count(vectorTaskCode));
    }

}
