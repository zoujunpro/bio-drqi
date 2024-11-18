package com.bio.drqi.manage.controller.project;

import com.bio.drqi.project.rsp.ProjectStepDetailRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.ProjectStepService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 项目步骤管理
 */
@RestController
@RequestMapping("/projectStep")
public class ProjectStepController {
    @Resource
    private ProjectStepService projectStepService;

    /**
     * 项目步骤查询
     *
     * @return
     */
    @GetMapping("/list")
    @WebLog(desc = "项目步骤查询")
    public ResponseResult<List<ProjectStepDetailRspDTO>> list() {
       return ResponseResult.getSuccess(projectStepService.list());
    }
}
