package com.bio.drqi.manage.controller.project;


import com.bio.drqi.manage.sample.req.*;
import com.bio.drqi.manage.sample.rsp.*;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.SampleTestService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 取样检测
 */
@RestController
@RequestMapping("/sampleTest")
public class SampleTestController {

    @Resource
    private SampleTestService sampleTestService;

    @Resource
    private OssService ossService;

    /**
     * 取样检测-分页查询取样申请
     *
     * @param sampleApplyListPageReqDTO
     * @return
     */
    @PostMapping("listPage")
    @RequirePermissions("project:sampleTest")
    @WebLog(desc = "取样检测-分页查询取样申请")
    public ResponseResult<PageInfo<SampleApplyRspDTO>> listPage(@Validated @RequestBody SampleApplyListPageReqDTO sampleApplyListPageReqDTO) {
        PageInfo<SampleApplyRspDTO> pageInfo = sampleTestService.listPage(sampleApplyListPageReqDTO);
        return ResponseResult.getSuccess(pageInfo);
    }

    /**
     * 取样检测-查询执行到某一步骤的取样申请
     *
     * @param currentStepCode
     * @return
     */
    @GetMapping("sampleApplyListAll")
    @WebLog(desc = "取样检测-查询执行到某一步骤的取样申请")
    public ResponseResult<List<SampleApplyRspDTO>> sampleApplyListAll(@Validated @RequestParam String currentStepCode) {
        List<SampleApplyRspDTO> list = sampleTestService.sampleApplyListAll(currentStepCode);
        return ResponseResult.getSuccess(list);
    }


    /**
     * 取样检测-查询某一个取样申请下取样明细
     *
     * @param sampleTestListDetailReqDTO
     * @return
     */
    @PostMapping("listDetail")
    @WebLog(desc = "取样检测-查询某一个取样申请下取样明细")
    public ResponseResult<PageInfo<SampleTestListDetailRspDTO>> listDetail(@Validated @RequestBody SampleTestListDetailReqDTO sampleTestListDetailReqDTO) {
        PageInfo<SampleTestListDetailRspDTO> resultList = sampleTestService.listDetail(sampleTestListDetailReqDTO);
        return ResponseResult.getSuccess(resultList);
    }


    /**
     * 取样检测-查询实施方案下取样检测信息
     *
     * @param sampleTestByVectorTaskReqDTO
     * @return
     */
    @PostMapping("listByVectorTask")
    @WebLog(desc = "取样检测-查询实施方案下取样检测信息")
    public ResponseResult<List<SampleApplyRspDTO>> listByVectorTask(@Validated @RequestBody SampleTestByVectorTaskReqDTO sampleTestByVectorTaskReqDTO) {
        return ResponseResult.getSuccess(sampleTestService.listByVectorTask(sampleTestByVectorTaskReqDTO));
    }

    /**
     * 取样检测-取样数据模板下载
     */
    @PostMapping("downSampleTemplate")
    @WebLog(desc = "取样检测-取样数据模板下载")
    public void downSampleTemplate(@Validated @RequestBody DownloadSampleTemplateReqDTO downloadSampleTemplateReqDTO, HttpServletResponse response) {
        sampleTestService.downSampleTemplate(downloadSampleTemplateReqDTO, response);
    }


    /**
     * 取样检测-上传取样数据模板
     */
    @PostMapping("uploadSampleTemplate")
    @WebLog(desc = "取样检测-上传取样数据模板")
    public ResponseResult<String> uploadSampleTemplate(@Validated @RequestBody UploadSampleTemplateReqDTO uploadSampleTemplateReqDTO) {
        sampleTestService.uploadSampleTemplate(uploadSampleTemplateReqDTO);
        return ResponseResult.getSuccess("结果处理成功");
    }

    /**
     * 取样检测-检测数据模板下载
     */
    @PostMapping("downTestTemplate")
    @WebLog(desc = "取样检测-检测数据模板下载")
    public void downTestTemplate(@Validated @RequestBody DownTestTemplateReqDTO downTestTemplateReqDTO, HttpServletResponse response) {
        sampleTestService.downTestTemplate(downTestTemplateReqDTO, response);
    }


    /**
     * 取样检测-上传检测数据
     */
    @PostMapping("uploadTestTemplate")
    @WebLog(desc = "取样检测-上传检测数据")
    public ResponseResult<String> uploadTestTemplate(@Validated @RequestBody UploadTestTemplateReqDTO uploadTestTemplateReqDTO) {
        sampleTestService.uploadTestTemplate(uploadTestTemplateReqDTO);
        return ResponseResult.getSuccess("成功");
    }


    /**
     * 取样检测-查询可审批的检测数据
     */
    @PostMapping("checkList")
    @WebLog(desc = "取样检测-检查列表")
    public ResponseResult<List<SampleTestListDetailRspDTO>> checkList(@Validated @RequestBody CheckListReqDTO checkListReqDTO) {
        return ResponseResult.getSuccess(sampleTestService.checkList(checkListReqDTO));
    }

    /**
     * 取样检测-重复取样模板下载
     */
    @GetMapping("downRepeatSampleApplyTemplate")
    @WebLog(desc = "取样检测-重复取样模板下载")
    public void downRepeatSampleApplyTemplate(HttpServletResponse response) {
        try {
            ossService.downloadFile(response, "template", "重复取样申请模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("重复取样模板下载失败，请联系管理员检测模板配置");
        }
    }

    /**
     * 取样检测-取样检测审批
     */
    @PostMapping("approveSampleResult")
    @WebLog(desc = "取样检测-取样结果审批")
    public ResponseResult<String> approveSampleResult(@Validated @RequestBody ApproveSampleResultReqDTO approveSampleResultReqDTO) {
        sampleTestService.approveSampleResult(approveSampleResultReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 取样检测-根据状态查询所有取样申请
     *
     * @param vectorTaskCode
     * @return
     */
    @GetMapping("findAllSampleCodeList")
    @WebLog(desc = "取样检测-根据状态查询所有取样申请")
    public ResponseResult<List<SampleCodeListRspDTO>> findAllSampleCodeList(@RequestParam String vectorTaskCode) {
        return ResponseResult.getSuccess(sampleTestService.findAllSampleCodeList(vectorTaskCode));
    }


    /**
     * 取样检测-下载填写鉴定引物模板
     *
     * @param response
     * @param applyNo
     * @return
     */
    @GetMapping("downIdentifyPrimerTemplate")
    @WebLog(desc = "取样检测-下载填写鉴定引物模板")
    public void downIdentifyPrimerTemplate(HttpServletResponse response, @RequestParam @Validated String applyNo) {
        sampleTestService.downIdentifyPrimerTemplate(response, applyNo);
    }

    /**
     * 取样检测-上传填写鉴定引物模板
     *
     * @return
     */
    @PostMapping("uploadIdentifyPrimerTemplate")
    @WebLog(desc = "取样检测-上传填写鉴定引物模板")
    public ResponseResult<String> uploadIdentifyPrimerTemplate(@RequestBody @Validated UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO) {
        sampleTestService.uploadIdentifyPrimerTemplate(uploadIdentifyPrimerTemplateReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 取样检测-取样标签排版预览
     *
     * @param applyNo
     * @return
     */
    @GetMapping("layoutPreview")
    @WebLog(desc = "取样检测-取样标签排版预览")
    public ResponseResult<LayoutPreviewRspDTO> layoutPreview(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(sampleTestService.layoutPreview(applyNo));
    }


    /**
     * 取样检测-取样标签排版确认
     *
     * @param layoutConfirmReqDTO
     * @return
     */
    @PostMapping("layoutConfirm")
    @WebLog(desc = "取样检测-取样标签排版确认")
    public ResponseResult layoutConfirm(@RequestBody @Validated LayoutConfirmReqDTO layoutConfirmReqDTO) {
        sampleTestService.layoutConfirm(layoutConfirmReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 取样检测-下载excel96孔板
     *
     * @return
     */
    @GetMapping("dowLayoutExcel")
    @WebLog(desc = "取样检测-下载excel96孔板")
    public void dowLayoutExcel(@RequestParam @Validated String applyNo, HttpServletResponse httpServletResponse) {
        sampleTestService.dowLayoutExcel(applyNo, httpServletResponse);
    }

    /**
     * 取样检测-统计数据
     *
     * @return
     */
    @GetMapping("countNumByApplyNo")
    @WebLog(desc = "取样检测-统计数据")
    public ResponseResult<CountNumByApplyNoRspDTO> countNumByApplyNo(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(sampleTestService.countNumByApplyNo(applyNo));
    }

    /**
     * 取样检测-生信结果核对模板下载
     */
    @PostMapping("downSampleTestBioInfoResultTemplate")
    @WebLog(desc = "取样检测-生信结果核对模板下载")
    public void downSampleTestBioInfoResultTemplate(HttpServletResponse response) {
        try {
            ossService.downloadFile(response, "template", "生信结果核对模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("生信结果核对模板下载失败，请联系管理员检测模板配置");
        }
    }

    /**
     * 取样检测-上传生信检测结果数据核对模板
     *
     * @param uploadBioInfoSampleTestResultReqDTO
     * @return
     */
    @PostMapping("uploadBioInfoSampleTestResult")
    @WebLog(desc = "取样检测-上传生信检测结果数据核对模板")
    public ResponseResult<String> uploadBioInfoSampleTestResult(@RequestBody UploadBioInfoSampleTestResultReqDTO uploadBioInfoSampleTestResultReqDTO) {
        sampleTestService.uploadBioInfoSampleTestResult(uploadBioInfoSampleTestResultReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 取样检测-生信检测结果查看
     *
     * @param id
     * @return
     */
    @GetMapping("queryBioInfoSampleTestResult")
    @WebLog(desc = "取样检测-生信检测结果查看")
    public ResponseResult<List<QueryBioInfoSampleTestResultRspDTO>> queryBioInfoSampleTestResult(@RequestParam Integer id) {
        return ResponseResult.getSuccess(sampleTestService.queryBioInfoSampleTestResult(id));
    }

    /**
     * 取样检测-生信检测结果确认
     *
     * @param bioInfoSampleTestResultConfirmReqDTO
     * @return
     */
    @PostMapping("bioInfoSampleTestResultConfirm")
    @WebLog(desc = "取样检测-生信检测结果确认")
    public ResponseResult<String> bioInfoSampleTestResultConfirm(@RequestBody BioInfoSampleTestResultConfirmReqDTO bioInfoSampleTestResultConfirmReqDTO) {
        sampleTestService.bioInfoSampleTestResultConfirm(bioInfoSampleTestResultConfirmReqDTO);
        return ResponseResult.getSuccess(null);
    }


    /**
     * 取样检测-同步生信检测结果数据
     *
     * @param id
     * @return
     */
    @GetMapping("synBioInfoSampleTestResult")
    @WebLog(desc = "取样检测-同步生信检测结果数据")
    public ResponseResult<String> synBioInfoSampleTestResult(@RequestParam Integer id) {
        sampleTestService.synBioInfoSampleTestResult(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 取样检测-生信检测结果数据详情
     *
     * @param bioInfoId
     * @return
     */
    @GetMapping("bioInfoSampleTestResultDetail")
    @WebLog(desc = "取样检测-生信检测结果数据详情")
    public ResponseResult<Object> bioInfoSampleTestResultDetail(@RequestParam Integer bioInfoId) {
        return ResponseResult.getSuccess(sampleTestService.bioInfoSampleTestResultDetail(bioInfoId));
    }


    /**
     * 取样检测-生信检测结果分页详情头
     *
     * @return
     */
    @GetMapping("bioInfoHead")
    @WebLog(desc = "取样检测-生信检测结果分页详情头")
    public ResponseResult<Integer> bioInfoHead(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(sampleTestService.bioInfoHead(applyNo));
    }

    /**
     * 取样检测-生信检测结果分页详情
     *
     * @return
     */
    @PostMapping("bioInfoPage")
    @WebLog(desc = "取样检测-生信检测结果分页详情")
    public ResponseResult<PageInfo<BioInfoPageRspDTO>> bioInfoPage(@RequestBody @Validated BioInfoPageReqDTO bioInfoPageReqDTO) {
        return ResponseResult.getSuccess(sampleTestService.bioInfoPage(bioInfoPageReqDTO));
    }

    /**
     * 取样检测-备注
     *
     * @return
     */
    @PostMapping("remark")
    @WebLog(desc = "取样检测-备注")
    public ResponseResult<String> remark(@RequestBody @Validated SampleRemarkReqDTO sampleRemarkReqDTO) {
        sampleTestService.remark(sampleRemarkReqDTO);
        return ResponseResult.getSuccess("成功");
    }


    /**
     * 取样检测-统计检测结果
     * @param applyNo
     * @return
     */
    @GetMapping("countCheckResult")
    @WebLog(desc = "取样检测-统计检测结果")
    public ResponseResult<List<CountCheckResultRspDTO>> countCheckResult(@RequestParam @Validated String applyNo){
        return ResponseResult.getSuccess(sampleTestService.countCheckResult(applyNo));
    }
}
