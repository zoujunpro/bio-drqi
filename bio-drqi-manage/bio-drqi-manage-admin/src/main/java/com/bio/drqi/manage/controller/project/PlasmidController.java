package com.bio.drqi.manage.controller.project;

import com.bio.drqi.manage.plasmid.req.PlasmidListPageReqDTO;
import com.bio.drqi.manage.plasmid.req.QueryPagePlasmidReqDTO;
import com.bio.drqi.manage.plasmid.rsp.PlasmidListPageRspDTO;
import com.bio.drqi.manage.plasmid.rsp.QueryPagePlasmidRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.PlasmidService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 质粒质检
 */
@RestController
@RequestMapping("/plasmid")
public class PlasmidController {

    @Resource
    private PlasmidService plasmidService;



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
        try {
            plasmidService.downPlasmidCheckTemplate(vectorTaskCode, response);
        } catch (Exception e) {
            throw new BusinessException("质粒上传模板下载失败，请联系管理员检测模板配置");
        }
    }

}
