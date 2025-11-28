package com.bio.drqi.manage.controller.plant;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.web.aspect.WebLog;

import com.bio.drqi.manage.plant.req.PlantDtlListDetailReqDTO;
import com.bio.drqi.manage.plant.req.PlantSingleStockListPageReqDTO;
import com.bio.drqi.manage.plant.req.PlantSingleStockQueryListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlCountRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlListDetailRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantSingleStockListPageRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantSingleStockQueryListRspDTO;
import com.bio.drqi.manage.service.plant.PlantSingleStockService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * cer苗库管理（有具体种植编号苗库）
 */
@RestController
@RequestMapping("plantSingleStock")
public class PlantSingleStockController {

    @Resource
    private PlantSingleStockService plantSingleStockService;

    @Resource
    private OssService ossService;


    /**
     * cer苗库管理（有具体种植编号苗库）-分页查询
     * @param plantSingleStockListPageReqDTO
     * @return
     */

    @PostMapping("/listPage")
    @WebLog(desc = "cer苗库管理（有具体种植编号苗库）-分页查询")
    public ResponseResult<PageInfo<PlantSingleStockListPageRspDTO>> listPage(@RequestBody  PlantSingleStockListPageReqDTO plantSingleStockListPageReqDTO) {
        return ResponseResult.getSuccess(plantSingleStockService.listPage(plantSingleStockListPageReqDTO));
    }

    /**
     * cer苗库管理（有具体种植编号苗库）-条件查询
     * @param plantSingleStockListPageReqDTO
     * @return
     */

    @PostMapping("/queryList")
    @WebLog(desc = "cer苗库管理（有具体种植编号苗库）-条件查询")
    public ResponseResult<List<PlantSingleStockQueryListRspDTO>> queryList(@RequestBody  PlantSingleStockQueryListReqDTO plantSingleStockListPageReqDTO) {
        return ResponseResult.getSuccess(plantSingleStockService.queryList(plantSingleStockListPageReqDTO));
    }

    /**
     * 种植明细-实施方案下分页查询
     *
     * @param plantDtlListDetailReqDTO
     * @return
     */
    @PostMapping("listByVectorTaskIdDetail")
    @WebLog(desc = "种植明细-实施方案下分页查询")
    public ResponseResult<PageInfo<PlantDtlListDetailRspDTO>> listByVectorTaskIdDetail(@Validated @RequestBody PlantDtlListDetailReqDTO plantDtlListDetailReqDTO) {
        return ResponseResult.getSuccess(plantSingleStockService.listByVectorTaskIdDetail(plantDtlListDetailReqDTO));
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
        return ResponseResult.getSuccess(plantSingleStockService.count(vectorTaskCode));
    }
}
