package com.bio.drqi.manage.controller.project;

import com.bio.drqi.manage.board.*;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.manage.service.project.ProjectBoardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * POC项目统计面板
 */
@RestController
@RequestMapping("projectBoard")
public class ProjectBoardController {

    @Resource
    private ProjectBoardService projectBoardService;

    /**
     * 统计任务（按照办理情况）
     *
     * @return
     */
    @GetMapping("/taskCount")
    public ResponseResult<ProjectTaskCountRspDTO> taskCount() {
        return ResponseResult.getSuccess(projectBoardService.taskCount());
    }

    /**
     * 统计移苗
     * @param year
     * @return
     */
    @GetMapping("/countTransByMonth")
    public ResponseResult<List<CountTransByMonthRspDTO>> countTransByMonth(@RequestParam @NotBlank(message = "请确认年份") String year) {
        return ResponseResult.getSuccess(projectBoardService.countTransByMonth(year));
    }
    /**
     * 统计取样
     * @param year
     * @return
     */
    @GetMapping("/countSampleByMonth")
    public ResponseResult<List<CountSampleByMonthRspDTO>> countSampleByMonth(@RequestParam @NotBlank(message = "请确认年份") String year) {
        return ResponseResult.getSuccess(projectBoardService.countSampleByMonth(year));
    }


    /**
     * 统计实施方案
     * @param vectorTaskListBoardReqDTO
     * @return
     */
    @PostMapping("/vectorTaskListBoard")
    public ResponseResult<List<VectorTaskListBoardRspDTO>> vectorTaskListBoard(@RequestBody @Validated VectorTaskListBoardReqDTO vectorTaskListBoardReqDTO) {
        return ResponseResult.getSuccess(projectBoardService.vectorTaskListBoard(vectorTaskListBoardReqDTO));
    }


}
