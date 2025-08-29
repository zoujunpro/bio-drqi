package com.bio.drqi.manage.controller.project;


import com.bio.drqi.manage.project.req.SubProjectListPageReqDTO;
import com.bio.drqi.manage.project.rsp.ProjectSpeciesLispRspDTO;
import com.bio.drqi.manage.project.rsp.SubProjectListPageRspDTO;
import com.bio.drqi.manage.project.rsp.SubProjectRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.SubProjectService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 子项目相关接口
 */
@RestController
@RequestMapping("/subProject")
public class SubProjectController {

    @Resource
    private SubProjectService subProjectService;

    /**
     * 子项目管理-分页查询
     *
     * @param subProjectListPageReqDTO
     * @return
     */
    @PostMapping("listPage")
    @WebLog(desc = "子项目管理-分页查询")
    public ResponseResult<PageInfo<SubProjectListPageRspDTO>> listPage(@Validated @RequestBody SubProjectListPageReqDTO subProjectListPageReqDTO) {
        return ResponseResult.getSuccess(subProjectService.listPage(subProjectListPageReqDTO));
    }


    /**
     * 子项目管理-列表查
     *
     * @param projectId
     * @return
     */
    @GetMapping("listByProject")
    @WebLog(desc = "子项目管理-列表查")
    public ResponseResult<List<SubProjectRspDTO>> list(@Validated @RequestParam @NotNull(message = "参数缺失") Integer projectId) {
        return ResponseResult.getSuccess(subProjectService.list(projectId));
    }

    /**
     * 子项目管理-查询子项目中所有物种
     */
    @GetMapping("/findSubProjectAllSpecies")
    @WebLog(desc = "子项目管理-查询子项目中所有物种")
    public ResponseResult<List<ProjectSpeciesLispRspDTO>> findSubProjectAllSpecies(@RequestParam String subProjectCode) {
        return ResponseResult.getSuccess(subProjectService.findSubProjectAllSpecies(subProjectCode));
    }

}
