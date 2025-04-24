package com.bio.drqi.manage.controller.project;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.plant.req.PlantDtlListDetailReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlListDetailRspDTO;
import com.bio.drqi.manage.sample.req.SampleTestListDetailReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleTestListDetailRspDTO;
import com.bio.drqi.manage.service.project.CerPlantDtlService;
import com.bio.drqi.manage.plant.req.PlantDtlListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlListRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 种植明细-分页查询
 */
@RestController
@RequestMapping("/plantDtl")
public class CerPlantDtlController {

    @Resource
    private CerPlantDtlService cerPlantDtlService;


    /**
     * 种植明细-分页查询
     * @param plantDtlListReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "种植明细-分页查询")
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



}
