package com.bio.drqi.manage.service;

import com.bio.drqi.enums.QueryTypeEnum;
import com.bio.drqi.seedtask.SeedInDataReqDTO;
import com.bio.drqi.seedtask.SeedTaskSeedNumRspDTO;
import com.bio.drqi.task.*;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

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


    PageInfo<SeedInStoreDTO.ExecuteFormContent> seedInData(@RequestParam @Validated SeedInDataReqDTO seedInDataReqDTO);

    List<SeedTaskSeedNumRspDTO> findAllSeedNum(Integer id);

    void temporarySave(BioTaskTemporarySaveReqDTO bioTaskTemporarySaveReqDTO);

    List<BioQueryAllTaskUserRspDTO> queryAllTaskUser( String taskCategory);

}
