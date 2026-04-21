package com.bio.drqi.manage.controller.project;

import com.bio.drqi.common.aspect.RequestLog;
import com.bio.drqi.manage.project.req.CerConversionAndTransConfirmReqDTO;
import com.bio.drqi.manage.project.req.ConversionAndTransDetailReqDTO;
import com.bio.drqi.manage.project.req.ConversionAndTransReqDTO;
import com.bio.drqi.manage.project.rsp.ConversionAndTransDetailRspDTO;
import com.bio.drqi.manage.project.rsp.ConversionAndTransRspDTO;
import com.bio.common.core.dto.ResponseResult;
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
 * 转化移苗
 */
@RestController
@RequestMapping("conversionAndTrans")
public class CerConversionAndTransController {

    @Resource
    private CerConversionAndTransService cerConversionAndTransService;
    /**
     * 分页查询
     * @param conversionAndTransReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "转化移苗-分页查询")
    @RequirePermissions("cer:conversionAndTrans:listPage")
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
     * 转化移苗，详情分页查询
     * @return
     */
    @PostMapping("/listPageDetail")
    @WebLog(desc = "转化移苗-详情分页查询")
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
        cerConversionAndTransService.downVectorTemplate(response);
    }

    /**
     * 转化移苗-移苗接受
     * @return
     */
    @PostMapping("/transAccept")
    @WebLog(desc = "转化移苗-移苗接受")
    @RequestLog("转化移苗-移苗接受")
    public ResponseResult<String> transAccept(@RequestBody @Validated CerConversionAndTransConfirmReqDTO cerConversionAndTransConfirmReqDTO){
        cerConversionAndTransService.transAccept(cerConversionAndTransConfirmReqDTO);
        return ResponseResult.getSuccess("成功");
    }
}
