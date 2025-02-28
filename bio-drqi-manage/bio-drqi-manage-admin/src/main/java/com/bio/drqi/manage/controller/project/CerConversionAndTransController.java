package com.bio.drqi.manage.controller.project;

import com.bio.drqi.manage.project.req.CerConversionAndTransConfirmReqDTO;
import com.bio.drqi.manage.project.req.ConversionAndTransDetailReqDTO;
import com.bio.drqi.manage.project.req.ConversionAndTransReqDTO;
import com.bio.drqi.manage.project.rsp.ConversionAndTransDetailRspDTO;
import com.bio.drqi.manage.project.rsp.ConversionAndTransRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.CerConversionAndTransService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 转化疫苗
 */
@RestController
@RequestMapping("conversionAndTrans")
public class CerConversionAndTransController {

    @Resource
    private CerConversionAndTransService cerConversionAndTransService;

    @Resource
    private OssService ossService;
    /**
     * 分页查询
     * @param conversionAndTransReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "转化疫苗-分页查询")
    @RequirePermissions("project:transplantData")
    public ResponseResult<PageInfo<ConversionAndTransRspDTO>> listPage(@RequestBody @Validated ConversionAndTransReqDTO conversionAndTransReqDTO) {
        return ResponseResult.getSuccess(cerConversionAndTransService.listPage(conversionAndTransReqDTO));
    }

    /**
     * 查询某一实施方案下移苗信息
     * @param
     * @return
     */
    @GetMapping("/listByVectorTask")
    @WebLog(desc = "查询某一实施方案下移苗信息")
    public ResponseResult<List<ConversionAndTransRspDTO>> listByVectorTask(@RequestParam @Validated  Integer vectorTaskId) {
        return ResponseResult.getSuccess(cerConversionAndTransService.listByVectorTask(vectorTaskId));
    }


    /**
     * 查询移苗申请下详情信息移苗详情
     * @return
     */
    @PostMapping("/listPageDetail")
    @WebLog(desc = "查询某一实施方案下移苗详情")
    public ResponseResult<PageInfo<ConversionAndTransDetailRspDTO>> listPageDetail(@RequestBody @Validated ConversionAndTransDetailReqDTO conversionAndTransDetailReqDTO) {
        return ResponseResult.getSuccess(cerConversionAndTransService.listPageDetail(conversionAndTransDetailReqDTO));
    }


    /**
     * 载体模板下载
     *
     * @param response
     */
    @GetMapping("/downVectorTemplate")
    public void downVectorTemplate(HttpServletResponse response) {
        try {
            ossService.downloadFile(response, "template", "取样检测数据移苗模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("移苗转化取样编号模板下载失败，请联系管理员检测模板配置");
        }
    }

    /**
     * 移苗接受
     * @return
     */
    @PostMapping("/transAccept")
    @WebLog(desc = "接收移苗")
    public ResponseResult<String> transAccept(@RequestBody @Validated CerConversionAndTransConfirmReqDTO cerConversionAndTransConfirmReqDTO){
        cerConversionAndTransService.transAccept(cerConversionAndTransConfirmReqDTO);
        return ResponseResult.getSuccess("成功");
    }
}
