package com.bio.drqi.manage.controller.project;


import com.bio.drqi.common.aspect.RequestLog;
import com.bio.drqi.manage.project.req.ProjectListReqDTO;
import com.bio.drqi.manage.project.rsp.*;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.ProjectService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 项目相关接口
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Resource
    private ProjectService projectService;

    /**
     * 项目管理-分页查询
     */

    @WebLog(desc = "项目管理-分页查询")
    @PostMapping("/listPage")
    @RequirePermissions("cer:project:listPage")
    public ResponseResult<PageInfo<ProjectListRspDTO>> listPage(@Validated @RequestBody ProjectListReqDTO projectListReqDTO) {
        PageInfo<ProjectListRspDTO> pageInfo = projectService.listPage(projectListReqDTO);
        return ResponseResult.getSuccess(pageInfo);
    }

    /**
     * 项目管理-查询列表
     */
    @GetMapping("/listBaseInfo")
    @WebLog(desc = "项目管理-查询列表")
    public ResponseResult<List<ListBaseInfoRspDTO>> listBaseInfo() {
        List<ListBaseInfoRspDTO> list = projectService.listBaseInfo();
        return ResponseResult.getSuccess(list);
    }

    /**
     * 项目管理-根据主键查询项目详情信息
     */
    @GetMapping("/detail")
    @WebLog(desc = "项目管理-根据主键查询项目详情信息")
    @RequirePermissions("project:data:projectDetail")
    public ResponseResult<ProjectListRspDTO> detail(@Validated @RequestParam @NotNull(message = "缺失主键") Integer id) {
        ProjectListRspDTO projectListRspDTO = projectService.detail(id);
        return ResponseResult.getSuccess(projectListRspDTO);
    }


    /**
     * 项目管理-查询所有项目基本信息
     */
    @GetMapping("/findAllProject")
    @WebLog(desc = "项目管理-查询所有项目基本信息")
    public ResponseResult<List<ProjectAllRspDTO>> findAllProject() {
        return ResponseResult.getSuccess(projectService.findAllProject());
    }


    /**
     * 项目管理-查询所有项目用户信息
     */
    @GetMapping("/findAllProjectAllUser")
    @WebLog(desc = "项目管理-查询项目所有负责人")
    public ResponseResult<List<ProjectUserAllRspDTO>> findAllProjectAllUser() {
        return ResponseResult.getSuccess(projectService.findAllProjectAllUser());
    }


    /**
     * 项目管理-暂停项目
     */
    @GetMapping("/stop")
    @WebLog(desc = "项目管理-暂停项目")
    @RequestLog("项目管理-暂停项目")
    public ResponseResult<String> stop(@RequestParam Integer id) {
        projectService.stop(id);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 项目管理-启动项目
     */
    @GetMapping("/start")
    @WebLog(desc = "项目管理-启动项目")
    @RequestLog("项目管理-启动项目")
    public ResponseResult<String> start(@RequestParam Integer id) {
        projectService.start(id);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 项目管理-完成项目
     */
    @GetMapping("/complete")
    @WebLog(desc = "项目管理-完成项目")
    @RequestLog("项目管理-完成项目")
    public ResponseResult<String> complete(@RequestParam Integer id) {
        projectService.complete(id);
        return ResponseResult.getSuccess("成功");
    }


}
