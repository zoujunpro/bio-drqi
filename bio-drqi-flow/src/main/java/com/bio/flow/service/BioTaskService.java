package com.bio.flow.service;


import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.flow.dto.*;
import com.bio.flow.enums.QueryTypeEnum;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface BioTaskService {
    BioTaskDtlTb start(BioTaskStartReqDTO bioTaskStartReqDTO);

    /**
     * 重新启动任务
     *
     * @param bioReStartTaskReqDTO
     * @return
     */
    BioTaskDtlTb reStartTask(BioReStartTaskReqDTO bioReStartTaskReqDTO);

    /**
     * 执行任务
     *
     * @param bioExecuteTaskReqDTO
     * @return
     */
    BioTaskDtlTb executeTask(BioExecuteTaskReqDTO bioExecuteTaskReqDTO);

    /**
     * 拒绝任务
     *
     * @param bioRejectTaskReqDTO
     * @return
     */
    BioTaskDtlTb rejectTask(BioRejectTaskReqDTO bioRejectTaskReqDTO);

    /**
     * 撤销任务
     *
     * @param bioRevokeTaskReqDTO
     * @return
     */
    BioTaskDtlTb revokeTask(BioRevokeTaskReqDTO bioRevokeTaskReqDTO);

    BioTaskDetailRspDTO detail(Integer id);

    BioTaskDetailRspDTO detailByTaskNum(String taskNum);

    PageInfo<BioTaskListPageRspDTO> listPage(BioTaskListPageReqDTO bioTaskListPageReqDTO, QueryTypeEnum queryTypeEnum);


    List<BioTaskTypeListRspDTO> listAllTaskType(String category);

    BioTaskTypeListRspDTO listOneTaskType(String taskTypeCode);


    List<QueryListRspDTO> queryList(QueryListReqDTO queryListReqDTO);


    void temporarySave(BioTaskTemporarySaveReqDTO bioTaskTemporarySaveReqDTO);

    List<BioQueryAllTaskUserRspDTO> queryAllTaskUser(String taskCategory);

    void exportExcel(BioExportExcelReqDTO bioExportExcelReqDTO, HttpServletResponse httpServletResponse);

}
