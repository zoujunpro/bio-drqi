package com.bio.flow.service;


import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.flow.dto.*;
import com.bio.flow.enums.QueryTypeEnum;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface BioTaskService {
    BioTaskDtlTb start(BioTaskStartReqDTO bioTaskStartReqDTO);

    /**
     * 重新启动任务
     *
     * @return
     * @param bioReStartTaskReqDTO
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


    BioTaskDtlTb backTask(BioBackTaskReqDTO bioRejectTaskReqDTO);

    /**
     * 撤销任务
     *
     * @param bioRevokeTaskReqDTO
     * @return
     */
    BioTaskDtlTb revokeTask(BioRevokeTaskReqDTO bioRevokeTaskReqDTO);

    void delete(Integer id);

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
