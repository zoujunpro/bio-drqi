package com.bio.flow.service;


import com.bio.drqi.enums.QueryTypeEnum;
import com.bio.flow.dto.*;
import com.github.pagehelper.PageInfo;


import java.util.List;

public interface BioTaskService {
    void start(BioTaskStartReqDTO bioTaskStartReqDTO);

    /**
     * 重新启动任务
     *
     * @param bioReStartTaskReqDTO
     * @return
     */
    void reStartTask(BioReStartTaskReqDTO bioReStartTaskReqDTO);

    /**
     * 执行任务
     *
     * @param bioExecuteTaskReqDTO
     * @return
     */
    void executeTask(BioExecuteTaskReqDTO bioExecuteTaskReqDTO);

    /**
     * 拒绝任务
     *
     * @param bioRejectTaskReqDTO
     * @return
     */
    void rejectTask(BioRejectTaskReqDTO bioRejectTaskReqDTO);

    /**
     * 撤销任务
     *
     * @param bioRevokeTaskReqDTO
     * @return
     */
    void revokeTask(BioRevokeTaskReqDTO bioRevokeTaskReqDTO);

    BioTaskDetailRspDTO detail(Integer id);

    BioTaskDetailRspDTO detailByTaskNum( String taskNum);

    PageInfo<BioTaskListPageRspDTO> listPage(BioTaskListPageReqDTO bioTaskListPageReqDTO, QueryTypeEnum queryTypeEnum);


    List<BioTaskTypeListRspDTO> listAllTaskType(String category);


    List<QueryListRspDTO> queryList(QueryListReqDTO queryListReqDTO);



    void temporarySave(BioTaskTemporarySaveReqDTO bioTaskTemporarySaveReqDTO);

    List<BioQueryAllTaskUserRspDTO> queryAllTaskUser( String taskCategory);

}
