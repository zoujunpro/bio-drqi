package com.bio.drqi.manage.controller.project;


import com.bio.drqi.manage.project.rsp.ProjectSpeciesLispRspDTO;
import com.bio.drqi.manage.project.rsp.SubProjectRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.SubProjectService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * 子项目列表查询
     *
     * @param projectId
     * @return
     */
    @GetMapping("listByProject")
    @WebLog(desc = "子项目列表查询")
    public ResponseResult<List<SubProjectRspDTO>> list(@Validated @RequestParam @NotNull(message = "参数缺失") Integer projectId) {
        return ResponseResult.getSuccess(subProjectService.list(projectId));
    }

    /**
     * 查询子项目中所有物种
     */
    @GetMapping("/findSubProjectAllSpecies")
    @WebLog(desc = "查询项目中所有物种")
    public ResponseResult<List<ProjectSpeciesLispRspDTO>> findSubProjectAllSpecies(@RequestParam String subProjectCode) {
        return ResponseResult.getSuccess(subProjectService.findSubProjectAllSpecies(subProjectCode));
    }

}
