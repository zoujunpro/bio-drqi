package com.bio.drqi.manage.controller.project;


import com.bio.drqi.vector.req.GetVectorTaskNumReqDTO;
import com.bio.drqi.vector.req.QueryPageVectorReqDTO;
import com.bio.drqi.vector.rsp.CerImplementationPlanBaseInfoRspDTO;
import com.bio.drqi.vector.rsp.StepListRspDTO;
import com.bio.drqi.vector.rsp.VectorListPageRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.aspect.RequestLog;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.manage.service.project.VectorTaskService;
import com.bio.drqi.vector.rsp.VectorTaskSpeciesRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 实施方案
 */

@RequestMapping("/implementationPlan")
@RestController
public class CerImplementationPlanController {

    @Resource
    private VectorTaskService vectorTaskService;

    @Resource
    private OssService ossService;

    /**
     * 分页查询实施方案
     */
    @PostMapping("/listPage")
    @WebLog(desc = "分页查询载体信息")
    @RequirePermissions("project:data:vector")
    public ResponseResult<PageInfo<VectorListPageRspDTO>> ListPage(@Validated @RequestBody QueryPageVectorReqDTO queryPageVectorReqDTO) {
        PageInfo<VectorListPageRspDTO> pageInfo = vectorTaskService.ListPage(queryPageVectorReqDTO);
        return ResponseResult.getSuccess(pageInfo);
    }


    /**
     * 查询子项目下所有实施方案
     */
    @GetMapping("/listBySubProject")
    @WebLog(desc = "查询子项目下所有实施方案")
    public ResponseResult<List<CerImplementationPlanBaseInfoRspDTO>> listBySubProject(@Validated @RequestParam Integer subProjectId) {
        List<CerImplementationPlanBaseInfoRspDTO> list = vectorTaskService.listBySubProject(subProjectId);
        return ResponseResult.getSuccess(list);
    }

    /**
     * 查询所有质粒质检他通过的实施方案
     */
    @GetMapping("/listAll")
    @WebLog(desc = "查询所有质粒质检他通过的实施方案")
    public ResponseResult<List<CerImplementationPlanBaseInfoRspDTO>> listAll() {
        List<CerImplementationPlanBaseInfoRspDTO> list = vectorTaskService.listAll();
        return ResponseResult.getSuccess(list);
    }

    /**
     * 查询所有审批通过的实施方案
     */
    @GetMapping("/listApproveAll")
    @WebLog(desc = "查询所有审批通过的实施方案")
    public ResponseResult<List<CerImplementationPlanBaseInfoRspDTO>> listApproveAll(@RequestParam String speciesCode) {
        List<CerImplementationPlanBaseInfoRspDTO> list = vectorTaskService.listApproveAll(speciesCode);
        return ResponseResult.getSuccess(list);
    }


    /**
     * 载体模板下载
     *
     * @param response
     */
    @GetMapping("/downVectorTemplate")
    public void downVectorTemplate(HttpServletResponse response) {
        try {
            ossService.downloadFile(response, "template", "质粒上传模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("质粒上传模板下载失败，请联系管理员检测模板配置");
        }
    }

    /**
     * 载体编号
     *
     * @param getVectorTaskNumReqDTO
     * @return
     */
    @PostMapping("/getTaskNum")
    @WebLog(desc = "获取工单编号")
    public ResponseResult<String> getTaskNum(@RequestBody @Validated GetVectorTaskNumReqDTO getVectorTaskNumReqDTO) {
        return ResponseResult.getSuccess(vectorTaskService.getTaskNum(getVectorTaskNumReqDTO));
    }

    /**
     * 查询实施方案步骤
     *
     * @param id
     * @return
     */
    @GetMapping("/stepList")
    @WebLog(desc = "查询实施方案步骤")
    public ResponseResult<List<StepListRspDTO>> stepList(@RequestParam Integer id) {
        return ResponseResult.getSuccess(vectorTaskService.stepList(id));
    }

    /**
     * 查询实施方案步骤(根据code)
     *
     * @param vectorTaskCode
     * @return
     */
    @GetMapping("/stepListByCode")
    @WebLog(desc = "查询实施方案步骤")
    public ResponseResult<List<StepListRspDTO>> stepListByCode(@RequestParam String vectorTaskCode) {
        return ResponseResult.getSuccess(vectorTaskService.stepListByCode(vectorTaskCode));
    }

    /**
     * 查询实施方案详情
     *
     * @param id
     * @return
     */
    @GetMapping("/detail")
    @WebLog(desc = "查询实施方案详情")
    public ResponseResult<VectorTaskAddDTO> detail(@RequestParam Integer id) {
        return ResponseResult.getSuccess(vectorTaskService.detail(id));
    }


    /**
     * 查询有转化的实施方案
     */
    @GetMapping("/listForTransForm")
    @WebLog(desc = "查询有转化的实施方案")
    public ResponseResult<List<CerImplementationPlanBaseInfoRspDTO>> listForTransForm() {
        List<CerImplementationPlanBaseInfoRspDTO> list = vectorTaskService.listForTransForm();
        return ResponseResult.getSuccess(list);
    }

    /**
     * 暂停实施方案
     */
    @GetMapping("/stop")
    @WebLog(desc = "暂停实施方案")
    @RequestLog("暂停实施方案")
    public ResponseResult<String> stop(@RequestParam Integer id) {
        vectorTaskService.stop(id);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 启动实施方案
     */
    @GetMapping("/start")
    @WebLog(desc = "启动实施方案")
    @RequestLog("启动实施方案")
    public ResponseResult<String> start(@RequestParam Integer id) {
        vectorTaskService.start(id);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 完成实施方案
     */
    @GetMapping("/complete")
    @WebLog(desc = "完成实施方案")
    @RequestLog("完成实施方案")
    public ResponseResult<String> complete(@RequestParam Integer id) {
        vectorTaskService.complete(id);
        return ResponseResult.getSuccess("成功");
    }


    /**
     * 查询瞬时验证编号
     *
     * @param vectorTaskCode
     * @return
     */
    @GetMapping("/getInstantVerifyTaskCode")
    @WebLog(desc = "查询瞬时验证编号")
    public ResponseResult<String> getInstantVerifyTaskCode(@RequestParam String vectorTaskCode) {
        return ResponseResult.getSuccess(vectorTaskService.getInstantVerifyTaskCode(vectorTaskCode));
    }


    /**
     * 查询实施方案所有物种
     *
     * @return
     */
    @GetMapping("/findAllSpecies")
    public ResponseResult<List<VectorTaskSpeciesRspDTO>> findAllSpecies() {
        return ResponseResult.getSuccess(vectorTaskService.findAllSpecies());
    }

}
