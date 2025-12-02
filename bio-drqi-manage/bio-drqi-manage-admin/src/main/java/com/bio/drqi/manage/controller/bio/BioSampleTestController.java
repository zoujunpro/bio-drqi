package com.bio.drqi.manage.controller.bio;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.bio.req.BioSampleTestListDetailReqDTO;
import com.bio.drqi.manage.bio.req.BioSampleTestUploadTestTemplateReqDTO;
import com.bio.drqi.manage.bio.rsp.BioSampleTestListDetailRspDTO;
import com.bio.drqi.manage.bio.req.BioSampleTestQueryBySampleCodeListReqDTO;
import com.bio.drqi.manage.bio.rsp.BioSampleTestQueryBySampleCodeListRspDTO;
import com.bio.drqi.manage.sample.req.*;
import com.bio.drqi.manage.sample.rsp.*;
import com.bio.drqi.manage.service.bio.BioSampleTestService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 分子取样检测
 */
@RestController
@RequestMapping("/bioSampleTest")
public class BioSampleTestController {

    @Resource
    private BioSampleTestService bioSampleTestService;

    @Resource
    private OssService ossService;
    /**
     * 分子取样检测-分页查询
     *
     * @param bioSampleTestListDetailReqDTO
     * @return
     */
    @PostMapping("listPage")
    @WebLog(desc = "分子取样检测-分页查询")
    public ResponseResult<PageInfo<BioSampleTestListDetailRspDTO>> listPage(@Validated @RequestBody BioSampleTestListDetailReqDTO bioSampleTestListDetailReqDTO) {
        PageInfo<BioSampleTestListDetailRspDTO> resultList = bioSampleTestService.listPage(bioSampleTestListDetailReqDTO);
        return ResponseResult.getSuccess(resultList);
    }



    /**
     * 分子取样检测-根据取样编号查询重复取样信息
     * @return
     */
    @PostMapping("/queryBySampleCodeList")
    @WebLog(desc = "分子取样检测-根据取样编号查询重复取样信息")
    public ResponseResult<List<BioSampleTestQueryBySampleCodeListRspDTO>>  queryBySampleCodeList(@RequestBody @Validated BioSampleTestQueryBySampleCodeListReqDTO bioSampleTestQueryBySampleCodeListReqDTO){
        return ResponseResult.getSuccess(bioSampleTestService.queryBySampleCodeList(bioSampleTestQueryBySampleCodeListReqDTO));
    }



    /**
     * 分子取样检测-下载重复取样模板
     * @param cloneFlag
     */
    @GetMapping("downRepeatSampleTemplate")
    @WebLog(desc = "分子取样检测-下载重复取样模板")
    public void downRepeatSampleTemplate(@RequestParam @Validated @NotBlank(message = "参数缺失") String cloneFlag, HttpServletResponse httpServletResponse) {
        bioSampleTestService.downRepeatSampleTemplate(cloneFlag,httpServletResponse);
    }


    /**
     * 分子取样检测-检测数据模板下载
     */
    @PostMapping("downTestTemplate")
    @WebLog(desc = "分子取样检测-检测数据模板下载")
    public void downTestTemplate(@Validated @RequestBody DownTestTemplateReqDTO downTestTemplateReqDTO, HttpServletResponse response) {
        bioSampleTestService.downTestTemplate(downTestTemplateReqDTO, response);
    }

    /**
     * 分子取样检测-检测数据模板上传
     */
    @PostMapping("uploadTestTemplate")
    @WebLog(desc = "分子取样检测-检测数据模板上传")
    public ResponseResult<String> uploadTestTemplate(@Validated @RequestBody BioSampleTestUploadTestTemplateReqDTO bioSampleTestUploadTestTemplateReqDTO) {
        bioSampleTestService.uploadTestTemplate(bioSampleTestUploadTestTemplateReqDTO);
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 分子取样检测-下载填写鉴定引物模板
     *
     * @param response
     * @param applyNo
     * @return
     */
    @GetMapping("downIdentifyPrimerTemplate")
    @WebLog(desc = "分子取样检测-下载填写鉴定引物模板")
    public void downIdentifyPrimerTemplate(HttpServletResponse response, @RequestParam @Validated String applyNo) {
        bioSampleTestService.downIdentifyPrimerTemplate(response, applyNo);
    }


    /**
     * 分子取样检测-上传填写鉴定引物模板
     *
     * @return
     */
    @PostMapping("uploadIdentifyPrimerTemplate")
    @WebLog(desc = "分子取样检测-上传填写鉴定引物模板")
    public ResponseResult<String> uploadIdentifyPrimerTemplate(@RequestBody @Validated UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO) {
        bioSampleTestService.uploadIdentifyPrimerTemplate(uploadIdentifyPrimerTemplateReqDTO);
        return ResponseResult.getSuccess("成功");
    }


    /**
     * 分子取样检测-取样标签排版预览
     *
     * @param applyNo
     * @return
     */
    @GetMapping("layoutPreview")
    @WebLog(desc = "分子取样检测-取样标签排版预览")
    public ResponseResult<LayoutPreviewRspDTO> layoutPreview(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(bioSampleTestService.layoutPreview(applyNo));
    }


    /**
     * 分子取样检测-取样标签排版确认
     *
     * @param layoutConfirmReqDTO
     * @return
     */
    @PostMapping("layoutConfirm")
    @WebLog(desc = "分子取样检测-取样标签排版确认")
    public ResponseResult layoutConfirm(@RequestBody @Validated LayoutConfirmReqDTO layoutConfirmReqDTO) {
        bioSampleTestService.layoutConfirm(layoutConfirmReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 分子取样检测-下载excel96孔板
     *
     * @return
     */
    @GetMapping("dowLayoutExcel")
    @WebLog(desc = "分子取样检测-下载excel96孔板")
    public void dowLayoutExcel(@RequestParam @Validated String applyNo, HttpServletResponse httpServletResponse) {
        bioSampleTestService.dowLayoutExcel(applyNo, httpServletResponse);
    }


    /**
     * 分子取样检测-统计数据
     *
     * @return
     */
    @GetMapping("countNumByApplyNo")
    @WebLog(desc = "分子取样检测-统计数据")
    public ResponseResult<CountNumByApplyNoRspDTO> countNumByApplyNo(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(bioSampleTestService.countNumByApplyNo(applyNo));
    }

    /**
     * 分子取样检测-生信结果核对模板下载
     */
    @PostMapping("downSampleTestBioInfoResultTemplate")
    @WebLog(desc = "分子取样检测-生信结果核对模板下载")
    public void downSampleTestBioInfoResultTemplate(HttpServletResponse response) {
        try {
            ossService.downloadFile(response, "template", "生信结果核对模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("生信结果核对模板下载失败，请联系管理员检测模板配置");
        }
    }

    /**
     * 分子取样检测-生信检测结果查看
     *
     * @param id
     * @return
     */
    @GetMapping("queryBioInfoSampleTestResult")
    @WebLog(desc = "分子取样检测-生信检测结果查看")
    public ResponseResult<List<QueryBioInfoSampleTestResultRspDTO>> queryBioInfoSampleTestResult(@RequestParam Integer id) {
        return ResponseResult.getSuccess(bioSampleTestService.queryBioInfoSampleTestResult(id));
    }

    /**
     * 分子取样检测-生信检测结果查看(根据取样编号查看)
     *
     * @param sampleCode
     * @return
     */
    @GetMapping("queryBioInfoSampleTestResultBySampleCode")
    @WebLog(desc = "分子取样检测-生信检测结果查看")
    public ResponseResult<List<QueryBioInfoSampleTestResultRspDTO>> queryBioInfoSampleTestResultBySampleCode(@RequestParam String sampleCode) {
        return ResponseResult.getSuccess(bioSampleTestService.queryBioInfoSampleTestResultBySampleCode(sampleCode));
    }



    /**
     * 分子取样检测-同步生信检测结果数据
     *
     * @param id
     * @return
     */
    @GetMapping("synBioInfoSampleTestResult")
    @WebLog(desc = "分子取样检测-同步生信检测结果数据")
    public ResponseResult<String> synBioInfoSampleTestResult(@RequestParam Integer id) {
        bioSampleTestService.synBioInfoSampleTestResult(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 分子取样检测-上传生信检测结果数据核对模板
     *
     * @param uploadBioInfoSampleTestResultReqDTO
     * @return
     */
    @PostMapping("uploadBioInfoSampleTestResult")
    @WebLog(desc = "分子取样检测-上传生信检测结果数据核对模板")
    public ResponseResult<String> uploadBioInfoSampleTestResult(@RequestBody UploadBioInfoSampleTestResultReqDTO uploadBioInfoSampleTestResultReqDTO) {
        bioSampleTestService.uploadBioInfoSampleTestResult(uploadBioInfoSampleTestResultReqDTO);
        return ResponseResult.getSuccess("ok");
    }
    /**
     * 分子取样检测-统计审核结果
     *
     * @param applyNo
     * @return
     */
    @GetMapping("countCheckResult")
    @WebLog(desc = "分子取样检测-统计审核结果")
    public ResponseResult<List<CountCheckResultRspDTO>> countCheckResult(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(bioSampleTestService.countCheckResult(applyNo));
    }

    /**
     * 分子取样检测-统计检测结果
     *
     * @param applyNo
     * @return
     */
    @GetMapping("countTestResult")
    @WebLog(desc = "分子取样检测-统计检测结果")
    public ResponseResult<CountTestResultRspDTO> countTestResult(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(bioSampleTestService.countTestResult(applyNo));
    }



    /**
     * 取样检测-取样检测审批
     */
    @PostMapping("approveSampleResult")
    @WebLog(desc = "取样检测-取样结果审批")
    public ResponseResult<String> approveSampleResult(@Validated @RequestBody ApproveSampleResultReqDTO approveSampleResultReqDTO) {
        bioSampleTestService.approveSampleResult(approveSampleResultReqDTO);
        return ResponseResult.getSuccess("成功");
    }

}
