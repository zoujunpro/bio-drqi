package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.*;
import com.bio.drqi.tc.rsp.*;
import com.bio.drqi.tc.service.TcSampleTestService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 田测取样检测管理
 */
@RestController
@RequestMapping("/tcSampleTest")
public class TcSampleTestController {

    @Resource
    private TcSampleTestService tcSampleTestService;

    @Resource
    private OssService ossService;


    /**
     * 田测取样检测管理-取样申请列表分页
     *
     * @param tcSampleTestListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "田测取样检测管理-取样申请列表")
    @RequirePermissions("tc:tcSampleTest:listPage")
    public ResponseResult<PageInfo<TcSampleTestListPageRspDTO>> listPage(@RequestBody @Validated TcSampleTestListPageReqDTO tcSampleTestListPageReqDTO) {
        return ResponseResult.getSuccess(tcSampleTestService.listPage(tcSampleTestListPageReqDTO));
    }

    /**
     * 田测取样检测管理-取样详情分页
     *
     * @param tcSampleTestListPageDetailReqDTO
     * @return
     */
    @PostMapping("/listPageDetail")
    @WebLog(desc = "田测取样检测管理-取样详情分页")
    public ResponseResult<PageInfo<TcSampleTestListPageDetailRspDTO>> listPageDetail(@RequestBody @Validated TcSampleTestListPageDetailReqDTO tcSampleTestListPageDetailReqDTO) {
        return ResponseResult.getSuccess(tcSampleTestService.listPageDetail(tcSampleTestListPageDetailReqDTO));
    }

    /**
     * 田测取样检测管理-根据试验编号查询取样申请信息
     *
     * @param experimentNum
     * @return
     */
    @GetMapping("/listByExperimentNum")
    @WebLog(desc = "田测取样检测管理-根据试验编号查询取样申请信息")
    public ResponseResult<List<String>> listByExperimentNum(@RequestParam @Validated  String experimentNum) {
        return ResponseResult.getSuccess(tcSampleTestService.listByExperimentNum(experimentNum));
    }




    /**
     * 田测取样检测管理-重复取样模板下载
     */
    @PostMapping("downTestRepeatSampleTemplate")
    @WebLog(desc = "田测取样检测管理-重复取样模板下载")
    public void downTestRepeatSampleTemplate(HttpServletResponse response) {
        try {
            ossService.downloadFile(response, "template", "田测重复取样模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("重复取样模板下载失败，请联系管理员检测模板配置");
        }
    }

    /**
     * 田测取样检测管理-检测数据模板下载
     */
    @PostMapping("downTestTemplate")
    @WebLog(desc = "田测取样检测管理-检测数据模板下载")
    public void downTestTemplate(@Validated @RequestBody TcSampleTestDownTestTemplateReqDTO tcSampleTestDownTestTemplateReqDTO, HttpServletResponse response) {
        tcSampleTestService.downTestTemplate(tcSampleTestDownTestTemplateReqDTO, response);
    }




    /**
     * 田测取样检测管理-上传检测数据
     */
    @PostMapping("uploadTestTemplate")
    @WebLog(desc = "田测取样检测管理-上传检测数据")
    public ResponseResult<String> uploadTestTemplate(@Validated @RequestBody TcSampleTestUploadTestTemplateReqDTO tcSampleTestUploadTestTemplateReqDTO) {
        tcSampleTestService.uploadTestTemplate(tcSampleTestUploadTestTemplateReqDTO);
        return ResponseResult.getSuccess("成功");
    }


    /**
     * 田测取样检测管理-下载填写鉴定引物模板
     *
     * @param response
     * @param applyNo
     * @return
     */
    @GetMapping("downIdentifyPrimerTemplate")
    @WebLog(desc = "田测取样检测管理-下载填写鉴定引物模板")
    public void downIdentifyPrimerTemplate( @RequestParam @Validated String applyNo ,HttpServletResponse response) {
        tcSampleTestService.downIdentifyPrimerTemplate(response, applyNo);
    }

    /**
     * 田测取样检测管理-上传填写鉴定引物模板
     *
     * @return
     */
    @PostMapping("uploadIdentifyPrimerTemplate")
    @WebLog(desc = "田测取样检测管理-上传填写鉴定引物模板")
    public ResponseResult<String> uploadIdentifyPrimerTemplate(@RequestBody @Validated TcSampleTestUploadIdentifyPrimerTemplateReqDTO tcSampleTestUploadIdentifyPrimerTemplateReqDTO) {
        tcSampleTestService.uploadIdentifyPrimerTemplate(tcSampleTestUploadIdentifyPrimerTemplateReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 田测取样检测管理-取样标签排版预览
     *
     * @param applyNo
     * @return
     */
    @GetMapping("layoutPreview")
    @WebLog(desc = "田测取样检测管理-取样标签排版预览")
    public ResponseResult<TcSampleTestLayoutPreviewRspDTO> layoutPreview(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(tcSampleTestService.layoutPreview(applyNo));
    }


    /**
     * 田测取样检测管理-取样标签排版确认
     *
     * @param tcSampleTestLayoutConfirmReqDTO
     * @return
     */
    @PostMapping("layoutConfirm")
    @WebLog(desc = "田测取样检测管理-取样标签排版确认")
    public ResponseResult layoutConfirm(@RequestBody @Validated TcSampleTestLayoutConfirmReqDTO tcSampleTestLayoutConfirmReqDTO) {
        tcSampleTestService.layoutConfirm(tcSampleTestLayoutConfirmReqDTO);
        return ResponseResult.getSuccess("成功");
    }


    /**
     * 田测取样检测管理-下载excel96孔板
     *
     * @return
     */
    @GetMapping("dowLayoutExcel")
    @WebLog(desc = "下载excel96孔板")
    public void dowLayoutExcel(@RequestParam @Validated String applyNo, HttpServletResponse httpServletResponse) {
        tcSampleTestService.dowLayoutExcel(applyNo, httpServletResponse);
    }


    /**
     * 田测取样检测管理-取样检测审批
     */
    @PostMapping("approveSampleResult")
    @WebLog(desc = "取样结果审批")
    public ResponseResult<String> approveSampleResult(@Validated @RequestBody TcSampleTestApproveSampleResultReqDTO tcSampleTestApproveSampleResultReqDTO) {
        tcSampleTestService.approveSampleResult(tcSampleTestApproveSampleResultReqDTO);
        return ResponseResult.getSuccess("成功");
    }


    /**
     * 田测取样检测管理-根据取样编号获取取样信息
     * @param tcSampleTestQueryListBySampleCodeListReqDTO
     * @return
     */
    @PostMapping("queryListBySampleCodeList")
    @WebLog(desc = "田测取样检测管理-根据取样编号获取取样信息")
    public ResponseResult<List<TcSampleTestQueryListBySampleCodeListRspDTO>>  queryListBySampleCodeList(@RequestBody @Validated TcSampleTestQueryListBySampleCodeListReqDTO tcSampleTestQueryListBySampleCodeListReqDTO){
        return ResponseResult.getSuccess(tcSampleTestService.queryListBySampleCodeList(tcSampleTestQueryListBySampleCodeListReqDTO));
    }


    /**
     * 生信结果核对模板下载
     */
    @PostMapping("downSampleTestBioInfoResultTemplate")
    @WebLog(desc = "田测取样检测管理-生信结果核对模板下载")
    public void downSampleTestBioInfoResultTemplate(HttpServletResponse response) {
        try {
            ossService.downloadFile(response, "template", "生信结果核对模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("生信结果核对模板下载失败，请联系管理员检测模板配置");
        }
    }

    /**
     * 上传生信检测结果数据核对模板
     *
     * @param tcSampleTestUploadBioInfoSampleTestResultReqDTO
     * @return
     */
    @PostMapping("uploadBioInfoSampleTestResult")
    @WebLog(desc = "田测取样检测管理-上传生信检测结果数据核对模板")
    public ResponseResult<String> uploadBioInfoSampleTestResult(@RequestBody TcSampleTestUploadBioInfoSampleTestResultReqDTO tcSampleTestUploadBioInfoSampleTestResultReqDTO) {
        tcSampleTestService.uploadBioInfoSampleTestResult(tcSampleTestUploadBioInfoSampleTestResultReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 生信检测结果查看
     *
     * @param id
     * @return
     */
    @GetMapping("queryBioInfoSampleTestResult")
    @WebLog(desc = "田测取样检测管理-生信检测结果查看")
    public ResponseResult<List<TcSampleTestQueryBioInfoSampleTestResultRspDTO>> queryBioInfoSampleTestResult(@RequestParam Integer id) {
        return ResponseResult.getSuccess(tcSampleTestService.queryBioInfoSampleTestResult(id));
    }

    /**
     * 生信检测结果确认
     *
     * @param tcSampleTestBioInfoSampleTestResultConfirmReqDTO
     * @return
     */
    @PostMapping("bioInfoSampleTestResultConfirm")
    @WebLog(desc = "田测取样检测管理-生信检测结果确认")
    public ResponseResult<String> bioInfoSampleTestResultConfirm(@RequestBody TcSampleTestBioInfoSampleTestResultConfirmReqDTO tcSampleTestBioInfoSampleTestResultConfirmReqDTO) {
        tcSampleTestService.bioInfoSampleTestResultConfirm(tcSampleTestBioInfoSampleTestResultConfirmReqDTO);
        return ResponseResult.getSuccess(null);
    }


    /**
     * 同步生信检测结果数据
     *
     * @param id
     * @return
     */
    @GetMapping("synBioInfoSampleTestResult")
    @WebLog(desc = "田测取样检测管理-同步生信检测结果数据")
    public ResponseResult<String> synBioInfoSampleTestResult(@RequestParam Integer id) {
        tcSampleTestService.synBioInfoSampleTestResult(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 生信检测结果数据详情
     *
     * @param bioInfoId
     * @return
     */
    @GetMapping("bioInfoSampleTestResultDetail")
    @WebLog(desc = "田测取样检测管理-生信检测结果数据详情")
    public ResponseResult<Object> bioInfoSampleTestResultDetail(@RequestParam Integer bioInfoId) {
        return ResponseResult.getSuccess(tcSampleTestService.bioInfoSampleTestResultDetail(bioInfoId));
    }


    /**
     * 生信检测结果分页详情头
     *
     * @return
     */
    @GetMapping("bioInfoHead")
    @WebLog(desc = "田测取样检测管理-生信检测结果分页详情头")
    public ResponseResult<Integer> bioInfoHead(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(tcSampleTestService.bioInfoHead(applyNo));
    }

    /**
     * 生信检测结果分页详情
     *
     * @return
     */
    @PostMapping("bioInfoPage")
    @WebLog(desc = "田测取样检测管理-生信检测结果分页详情")
    public ResponseResult<PageInfo<TcSampleTestBioInfoPageRspDTO>> bioInfoPage(@RequestBody @Validated TcSampleTestBioInfoPageReqDTO tcSampleTestBioInfoPageReqDTO) {
        return ResponseResult.getSuccess(tcSampleTestService.bioInfoPage(tcSampleTestBioInfoPageReqDTO));
    }


}
