package com.bio.drqi.manage.controller.project;

import com.bio.drqi.common.aspect.RequestLog;
import com.bio.drqi.manage.timePlan.VectorTaskTimePlanAddReqDTO;
import com.bio.drqi.manage.timePlan.VectorTaskTimePlanExportReqDTO;
import com.bio.drqi.manage.timePlan.VectorTaskTimePlanListRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.CerImplementationTimePlanService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 实施方案预估时间
 */
@RestController
@RequestMapping("vectorTaskTimePlan")
public class CerImplementationTimePlanController {

    @Resource
    private CerImplementationTimePlanService cerImplementationTimePlanService;

    /**
     * 列表查询
     *
     * @param vectorTaskCode
     * @return
     */
    @GetMapping("list")
    public ResponseResult<VectorTaskTimePlanListRspDTO> list(@RequestParam String vectorTaskCode) {
        return ResponseResult.getSuccess(cerImplementationTimePlanService.list(vectorTaskCode));
    }

    /**
     * 添加实施方案预估时间
     *
     * @param vectorTaskTimePlanAddReqDTO
     * @return
     */
    @PostMapping("add")
    @WebLog(desc = "添加实施方案预估时间")
    @RequestLog("添加实施方案预估时间")
    public ResponseResult<String> add(@RequestBody VectorTaskTimePlanAddReqDTO vectorTaskTimePlanAddReqDTO) {
        cerImplementationTimePlanService.add(vectorTaskTimePlanAddReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 实时方案预估时间导出
     *
     * @param vectorTaskTimePlanExportReqDTO
     */
    @PostMapping("/exportExcel")
    @WebLog(desc = "实时方案预估时间导出")
    public void exportExcel(@RequestBody VectorTaskTimePlanExportReqDTO vectorTaskTimePlanExportReqDTO, HttpServletResponse httpServletResponse) {
        cerImplementationTimePlanService.exportExcel(vectorTaskTimePlanExportReqDTO, httpServletResponse);
    }
}
