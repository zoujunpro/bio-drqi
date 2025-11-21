package com.bio.drqi.manage.controller.bio;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.bio.req.BioSampleTestUploadTestTemplateReqDTO;
import com.bio.drqi.manage.sample.req.DownTestTemplateReqDTO;
import com.bio.drqi.manage.sample.req.LayoutConfirmReqDTO;
import com.bio.drqi.manage.sample.req.UploadIdentifyPrimerTemplateReqDTO;
import com.bio.drqi.manage.sample.rsp.*;
import com.bio.drqi.manage.service.bio.BioSampleTestService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/bioSampleTest")
public class BioSampleTestController {

    @Resource
    private BioSampleTestService bioSampleTestService;

    @Resource
    private OssService ossService;

    @GetMapping("downRepeatSampleTemplate")
    @WebLog(desc = "分子取样检测-下载重复取样模板")
    public void downRepeatSampleTemplate(HttpServletResponse httpServletResponse) {
        bioSampleTestService.downRepeatSampleTemplate(httpServletResponse);
    }


    /**
     * 取样检测-检测数据模板下载
     */
    @PostMapping("downTestTemplate")
    @WebLog(desc = "取样检测-检测数据模板下载")
    public void downTestTemplate(@Validated @RequestBody DownTestTemplateReqDTO downTestTemplateReqDTO, HttpServletResponse response) {
        bioSampleTestService.downTestTemplate(downTestTemplateReqDTO, response);
    }

    /**
     * 取样检测-检测数据模板下载
     */
    @PostMapping("uploadTestTemplate")
    @WebLog(desc = "取样检测-检测数据模板下载")
    public void uploadTestTemplate(@Validated @RequestBody BioSampleTestUploadTestTemplateReqDTO bioSampleTestUploadTestTemplateReqDTO) {
        bioSampleTestService.uploadTestTemplate(bioSampleTestUploadTestTemplateReqDTO);
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
        bioSampleTestService.downIdentifyPrimerTemplate(response,applyNo);
    }


    /**
     * 取样检测-上传填写鉴定引物模板
     *
     * @return
     */
    @PostMapping("uploadIdentifyPrimerTemplate")
    @WebLog(desc = "取样检测-上传填写鉴定引物模板")
    public ResponseResult<String> uploadIdentifyPrimerTemplate(@RequestBody @Validated UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO) {
        bioSampleTestService.uploadIdentifyPrimerTemplate(uploadIdentifyPrimerTemplateReqDTO);
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
        return ResponseResult.getSuccess(bioSampleTestService.layoutPreview(applyNo));
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
        bioSampleTestService.layoutConfirm(layoutConfirmReqDTO);
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
        bioSampleTestService.dowLayoutExcel(applyNo, httpServletResponse);
    }


    /**
     * 取样检测-统计数据
     *
     * @return
     */
    @GetMapping("countNumByApplyNo")
    @WebLog(desc = "取样检测-统计数据")
    public ResponseResult<CountNumByApplyNoRspDTO> countNumByApplyNo(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(bioSampleTestService.countNumByApplyNo(applyNo));
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
     * 取样检测-生信检测结果查看
     *
     * @param id
     * @return
     */
    @GetMapping("queryBioInfoSampleTestResult")
    @WebLog(desc = "取样检测-生信检测结果查看")
    public ResponseResult<List<QueryBioInfoSampleTestResultRspDTO>> queryBioInfoSampleTestResult(@RequestParam Integer id) {
        return ResponseResult.getSuccess(bioSampleTestService.queryBioInfoSampleTestResult(id));
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
        bioSampleTestService.synBioInfoSampleTestResult(id);
        return ResponseResult.getSuccess("ok");
    }
    /**
     * 取样检测-统计审核结果
     *
     * @param applyNo
     * @return
     */
    @GetMapping("countCheckResult")
    @WebLog(desc = "取样检测-统计审核结果")
    public ResponseResult<List<CountCheckResultRspDTO>> countCheckResult(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(bioSampleTestService.countCheckResult(applyNo));
    }

    /**
     * 取样检测-统计检测结果
     *
     * @param applyNo
     * @return
     */
    @GetMapping("countTestResult")
    @WebLog(desc = "取样检测-统计检测结果")
    public ResponseResult<CountTestResultRspDTO> countTestResult(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(bioSampleTestService.countTestResult(applyNo));
    }

}
