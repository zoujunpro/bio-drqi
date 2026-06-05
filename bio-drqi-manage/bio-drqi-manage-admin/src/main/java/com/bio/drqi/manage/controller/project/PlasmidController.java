package com.bio.drqi.manage.controller.project;

import com.bio.drqi.manage.plasmid.req.PlasmidListPageReqDTO;
import com.bio.drqi.manage.plasmid.req.QueryPagePlasmidReqDTO;
import com.bio.drqi.manage.plasmid.rsp.PlasmidListPageRspDTO;
import com.bio.drqi.manage.plasmid.rsp.QueryPagePlasmidRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.common.CommonService;
import com.bio.drqi.manage.service.project.PlasmidService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

/**
 * 质粒质检
 */
@RestController
@RequestMapping("/plasmid")
public class PlasmidController {

    @Resource
    private PlasmidService plasmidService;

    @Resource
    private CommonService commonService;



    /**
     * 质粒质检-分页查询
     */
    @PostMapping("listPage")
    @WebLog(desc = "质粒质检-分页查询")
    @RequirePermissions("cer:plasmid:listPage")
    public ResponseResult<PageInfo<PlasmidListPageRspDTO>> listPage(@Validated @RequestBody PlasmidListPageReqDTO plasmidListPageReqDTO) {
        return ResponseResult.getSuccess(plasmidService.listPage(plasmidListPageReqDTO));
    }

    /**
     * 质粒质检-查询该项目下质粒
     */
    @PostMapping("listByVectorTask")
    @WebLog(desc = "质粒质检-查询该项目下质粒")
    @RequirePermissions("project:data:plasmid")
    public ResponseResult<QueryPagePlasmidRspDTO> listByVectorTask(@Validated @RequestBody QueryPagePlasmidReqDTO queryPagePlasmidReqDTO) {
        return ResponseResult.getSuccess(plasmidService.listByVectorTask(queryPagePlasmidReqDTO));
    }


    /**
     * 质粒质检-质粒质检模板下载
     *
     * @param response
     */
    @GetMapping("/downPlasmidCheckTemplate")
    @WebLog(desc = "质粒质检-质粒质检模板下载")
    public void downPlasmidCheckTemplate(@RequestParam @Validated String vectorTaskCode, HttpServletResponse response) {
        plasmidService.downPlasmidCheckTemplate(vectorTaskCode, response);
    }

    /**
     * 农杆菌信息查询
     *
     * @param tjAgroLocation
     * @return
     */
    @GetMapping("/getAgrobacteriumDetail")
    @WebLog(desc = "农杆菌信息查询")
    public ResponseResult<Object> getAgrobacteriumDetail(@RequestParam @Validated @NotBlank(message = "参数缺失:冰箱位置") String tjAgroLocation) {
        return ResponseResult.getSuccess(commonService.getAgrobacteriumDetail(tjAgroLocation));
    }

}
