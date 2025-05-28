package com.bio.flow.controller;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.common.aspect.RequestLog;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.flow.dto.*;
import com.bio.flow.enums.QueryTypeEnum;
import com.bio.flow.service.BioTaskService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 任务管理
 */
@RestController
@RequestMapping("task")
@Slf4j
public class BioTaskController {


    @Resource
    private BioTaskService bioTaskService;


    /**
     * 任务启动
     *
     * @param bioTaskStartReqDTO
     * @return
     */
    @PostMapping("start")
    @WebLog(desc = "任务启动")
    @RequestLog("任务启动")
    public ResponseResult<BioTaskDtlTb> start(@Validated @RequestBody BioTaskStartReqDTO bioTaskStartReqDTO) {
        return ResponseResult.getSuccess(bioTaskService.start(bioTaskStartReqDTO));
    }

    /**
     * 重新启动任务
     *
     * @param bioReStartTaskReqDTO
     * @return
     */
    @PostMapping("/reStartTask")
    @WebLog(desc = "重新启动任务")
    @RequestLog("重新启动任务")
    public ResponseResult<BioTaskDtlTb> reStartTask(@RequestBody @Validated BioReStartTaskReqDTO bioReStartTaskReqDTO) {
        return ResponseResult.getSuccess(bioTaskService.reStartTask(bioReStartTaskReqDTO));
    }

    /**
     * 执行任务
     *
     * @param bioExecuteTaskReqDTO
     * @return
     */
    @PostMapping("/execute")
    @WebLog(desc = "执行任务")
    @RequestLog("执行任务")
    public ResponseResult<BioTaskDtlTb> executeTask(@RequestBody @Validated BioExecuteTaskReqDTO bioExecuteTaskReqDTO) {
        return ResponseResult.getSuccess(bioTaskService.executeTask(bioExecuteTaskReqDTO));
    }

    /**
     * 拒绝任务
     *
     * @param bioRejectTaskReqDTO
     * @return
     */
    @PostMapping("/reject")
    @WebLog(desc = "拒绝任务")
    @RequestLog("拒绝任务")
    public ResponseResult<BioTaskDtlTb> rejectTask(@RequestBody @Validated BioRejectTaskReqDTO bioRejectTaskReqDTO) {
        return ResponseResult.getSuccess(bioTaskService.rejectTask(bioRejectTaskReqDTO));
    }

    /**
     * 撤销任务
     *
     * @param bioRevokeTaskReqDTO
     * @return
     */
    @PostMapping("/revoke")
    @WebLog(desc = "撤销任务")
    @RequestLog("撤销任务")
    public ResponseResult<BioTaskDtlTb> revokeTask(@RequestBody @Validated BioRevokeTaskReqDTO bioRevokeTaskReqDTO) {
        return ResponseResult.getSuccess(bioTaskService.revokeTask(bioRevokeTaskReqDTO));
    }

    /**
     * 详情接口
     *
     * @param id
     * @return
     */
    @GetMapping("/detail")
    @WebLog(desc = "详情接口")
    public ResponseResult<BioTaskDetailRspDTO> detail(@RequestParam @Validated Integer id) {
        return ResponseResult.getSuccess(bioTaskService.detail(id));
    }

    /**
     * 详情接口-根据编号查询
     *
     * @param taskNum
     * @return
     */
    @GetMapping("/detailByTaskNum")
    @WebLog(desc = "详情接口-根据编号查询")
    public ResponseResult<BioTaskDetailRspDTO> detailByTaskNum(@RequestParam @Validated String taskNum) {
        return ResponseResult.getSuccess(bioTaskService.detailByTaskNum(taskNum));
    }

    /**
     * 分页查询-全部数据
     *
     * @param bioTaskListPageReqDTO
     * @return
     */
    @PostMapping("listPageAllProcess")
    @WebLog(desc = "分页查询-全部数据")
    @RequirePermissions("task:allprocess")
    public ResponseResult<PageInfo<BioTaskListPageRspDTO>> listPageAll(@RequestBody BioTaskListPageReqDTO bioTaskListPageReqDTO) {
        return ResponseResult.getSuccess(bioTaskService.listPage(bioTaskListPageReqDTO, QueryTypeEnum.TYPE_1));
    }


    /**
     * 分页查询-我发起的
     *
     * @param bioTaskListPageReqDTO
     * @return
     */
    @PostMapping("listPageApplyProcess")
    @WebLog(desc = "分页查询-我发起的")
    @RequirePermissions("task:applyprocess")
    public ResponseResult<PageInfo<BioTaskListPageRspDTO>> listPageMyApply(@RequestBody BioTaskListPageReqDTO bioTaskListPageReqDTO) {
        return ResponseResult.getSuccess(bioTaskService.listPage(bioTaskListPageReqDTO,QueryTypeEnum.TYPE_3));
    }


    /**
     * 分页查询-我代办的
     *
     * @param bioTaskListPageReqDTO
     * @return
     */
    @PostMapping("listPageHandlingProcess")
    @WebLog(desc = "分页查询-我代办的")
    @RequirePermissions("task:handlingprocess")
    public ResponseResult<PageInfo<BioTaskListPageRspDTO>> listPageMyApprove(@RequestBody BioTaskListPageReqDTO bioTaskListPageReqDTO) {
        return ResponseResult.getSuccess(bioTaskService.listPage(bioTaskListPageReqDTO, QueryTypeEnum.TYPE_2));
    }
    /**
     * 分页查询-已办理的
     *
     * @param bioTaskListPageReqDTO
     * @return
     */
    @PostMapping("listPageHandleProcess")
    @WebLog(desc = "分页查询-已办理的")
    @RequirePermissions("task:handleprocess")
    public ResponseResult<PageInfo<BioTaskListPageRspDTO>> listPageMyDeal(@RequestBody BioTaskListPageReqDTO bioTaskListPageReqDTO) {
        return ResponseResult.getSuccess(bioTaskService.listPage(bioTaskListPageReqDTO,QueryTypeEnum.TYPE_4));
    }


    /**
     * 根据列表查询所有工单配置
     * @param category
     * @return
     */
    @GetMapping("listAllTaskType")
    @WebLog(desc = "查询所有工单类型")
    public ResponseResult<List<BioTaskTypeListRspDTO>> listAllTaskType(@RequestParam  String category) {
        return ResponseResult.getSuccess(bioTaskService.listAllTaskType(category));
    }


    /**
     * 根据任务类型查询某一个工单配置
     * @param taskTypeCode
     * @return
     */
    @GetMapping("listOneTaskType")
    @WebLog(desc = "查询所有工单类型")
    public ResponseResult<BioTaskTypeListRspDTO> listOneTaskType(@RequestParam  String taskTypeCode) {
        return ResponseResult.getSuccess(bioTaskService.listOneTaskType(taskTypeCode));
    }

    /**
     * 工单条件查询(目前无调用)
     * @param queryListReqDTO
     * @return
     */
    @PostMapping("queryList")
    @WebLog(desc = "工单条件查询")
    public ResponseResult<List<QueryListRspDTO>> queryList(@RequestBody @Validated QueryListReqDTO queryListReqDTO){
        return ResponseResult.getSuccess(bioTaskService.queryList(queryListReqDTO));
    }





    /**
     * 任务临时保存
     * @param bioTaskTemporarySaveReqDTO
     * @return
     */
    @PostMapping("/temporarySave")
    @WebLog(desc = "任务临时保存")
    @RequestLog("临时保存任务")
    public ResponseResult<String> temporarySave(@RequestBody @Validated BioTaskTemporarySaveReqDTO bioTaskTemporarySaveReqDTO) {
        bioTaskService.temporarySave(bioTaskTemporarySaveReqDTO);
        return ResponseResult.getSuccess(null);
    }

    /**
     *查询工单发起人员
     * @param taskCategory
     * @return
     */
    @GetMapping("/queryAllTaskUser")
    @WebLog(desc = "查询工单发起人员")
    public ResponseResult<List<BioQueryAllTaskUserRspDTO>>  queryAllTaskUser(@RequestParam String taskCategory){
        return ResponseResult.getSuccess(bioTaskService.queryAllTaskUser(taskCategory));
    }

    /**
     *导出任务工单
     * @param bioExportExcelReqDTO
     * @return
     */
    @PostMapping("/exportExcel")
    @WebLog(desc = "导出任务工单")
    @RequestLog("导出任务工单")
    public void  exportExcel(@RequestBody BioExportExcelReqDTO bioExportExcelReqDTO, HttpServletResponse httpServletResponse){
        bioTaskService.exportExcel(bioExportExcelReqDTO,httpServletResponse);
    }
}
