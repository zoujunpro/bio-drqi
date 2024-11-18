package com.bio.drqi.manage.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserBaseInfoRspDTO;

import com.bio.cer.enums.BioTaskStatusEnum;
import com.bio.cer.enums.QueryTypeEnum;
import com.bio.cer.enums.SeedTaskTypeEnum;

import com.bio.cer.seedtask.SeedInDataReqDTO;
import com.bio.cer.seedtask.SeedTaskSeedNumRspDTO;
import com.bio.cer.task.*;
import com.bio.cer.util.PaginationHelper;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.SpringUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.BioTaskConf;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.SeedStockInLog;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.manage.dto.seed.SeedOutDTO;
import com.bio.drqi.manage.listener.CerProjectTaskListener;
import com.bio.drqi.manage.listener.EventType;
import com.bio.drqi.manage.service.task.BaseTaskService;
import com.bio.drqi.mapper.BioTaskConfMapper;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.drqi.mapper.SeedStockInLogMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.easyflow.engine.entity.FlowHisInstanceTb;
import com.easyflow.engine.enums.InstanceState;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BioTaskServiceImpl implements BioTaskService {
    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private BioTaskConfMapper bioTaskConfMapper;

    @Resource
    private FlowService flowService;

    @Resource
    private CerProjectTaskListener cerProjectTaskListener;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;


    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 120000)
    public void start(BioTaskStartReqDTO bioTaskStartReqDTO) {
        BioTaskConf bioTaskConf = bioTaskConfMapper.selectOneByTaskTypeCode(bioTaskStartReqDTO.getTaskType());
        if (bioTaskConf == null) {
            throw new BusinessException("任务类型参数错误");
        }
        /**
         * 初始化任务
         */
        BioTaskDtlTb bioTaskDtlTb = initTask(bioTaskStartReqDTO, bioTaskConf);

        /**
         * 个性化表单处理
         */
        BaseTaskService baseTaskService = SpringUtils.getBean(bioTaskStartReqDTO.getTaskType());
        baseTaskService.taskCheck(bioTaskDtlTb);
        /**
         * 执行工作流
         */
        FlowHisInstanceTb flowHisInstanceTb = flowService.start(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskConf.getProcessId(), flowService.getArgs(bioTaskStartReqDTO.getFormObject(), bioTaskStartReqDTO.getTaskType()), null, bioTaskStartReqDTO.getSelfFlowActorList(), bioTaskConf.getTaskTypeName());

        /**
         * 填补数据
         */
        bioTaskDtlTb.setInstanceId(flowHisInstanceTb.getId());
        bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_1.status);
        /**
         * 流程执行后判断流程状态
         */
        afterFLow(bioTaskDtlTb, flowHisInstanceTb);


    }


    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 120000)
    public void reStartTask(BioReStartTaskReqDTO bioReStartTaskReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioReStartTaskReqDTO.getId());
        Assert.notNull(bioTaskDtlTb, "不存在此任务");

        BioTaskConf bioTaskConf = bioTaskConfMapper.selectOneByTaskTypeCode(bioTaskDtlTb.getTaskTypeCode());
        if (bioTaskConf == null) {
            throw new BusinessException("任务类型参数错误");
        }
        bioTaskDtlTb.setTaskDesc(bioReStartTaskReqDTO.getTaskDesc());
        bioTaskDtlTb.setTaskForm(bioReStartTaskReqDTO.getFormObject());
        bioTaskDtlTb.setRefTaskNum(bioReStartTaskReqDTO.getRefTaskNum());

        /**
         * 个性化表单处理
         */
        BaseTaskService baseTaskService = SpringUtils.getBean(bioTaskConf.getTaskTypeCode());
        baseTaskService.taskCheck(bioTaskDtlTb);

        /**
         * 执行工作流
         */
        FlowHisInstanceTb flowHisInstanceTb = flowService.start(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskConf.getProcessId(), flowService.getArgs(bioReStartTaskReqDTO.getFormObject(), bioTaskDtlTb.getTaskTypeCode()), null, bioReStartTaskReqDTO.getSelfFlowActorList(), bioTaskConf.getTaskTypeName());

        /**
         * 填补数据
         */
        bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_1.status);
        bioTaskDtlTb.setInstanceId(flowHisInstanceTb.getId());
        bioTaskDtlTb.setUpdateTime(new Date());
        /**
         * 流程执行后判断流程状态
         */
        afterFLow(bioTaskDtlTb, flowHisInstanceTb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 120000)
    public void executeTask(BioExecuteTaskReqDTO bioExecuteTaskReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioExecuteTaskReqDTO.getId());
        Assert.notNull(bioTaskDtlTb, "不存在此任务");
        if (StringUtils.isNotEmpty(bioExecuteTaskReqDTO.getFormObject())) {
            bioTaskDtlTb.setTaskForm(bioExecuteTaskReqDTO.getFormObject());
        }

        FlowHisInstanceTb flowHisInstanceTb = flowService.execute(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskDtlTb.getInstanceId(), flowService.getArgs(bioTaskDtlTb.getTaskForm(), bioTaskDtlTb.getTaskTypeCode()), null);

        /**
         * 流程执行后判断流程状态
         */
        afterFLow(bioTaskDtlTb, flowHisInstanceTb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 120000)
    public void rejectTask(BioRejectTaskReqDTO bioRejectTaskReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioRejectTaskReqDTO.getId());
        Assert.notNull(bioTaskDtlTb, "不存在此任务");

        FlowHisInstanceTb flowHisInstanceTb = flowService.reject(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskDtlTb.getInstanceId(), null, bioRejectTaskReqDTO.getReason());
        /**
         * 流程执行后判断流程状态
         */
        afterFLow(bioTaskDtlTb, flowHisInstanceTb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 120000)
    public void revokeTask(BioRevokeTaskReqDTO bioRevokeTaskReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioRevokeTaskReqDTO.getId());
        Assert.notNull(bioTaskDtlTb, "不存在此任务");

        FlowHisInstanceTb flowHisInstanceTb = flowService.revoke(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskDtlTb.getInstanceId(), bioRevokeTaskReqDTO.getReason());
        /**
         * 流程执行后判断流程状态
         */
        afterFLow(bioTaskDtlTb, flowHisInstanceTb);
    }

    @Override
    public BioTaskDetailRspDTO detail(Integer id) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(id);
        BioTaskDetailRspDTO bioTaskDetailRspDTO = BeanUtil.copyProperties(bioTaskDtlTb, BioTaskDetailRspDTO.class);
        bioTaskDetailRspDTO.setInstanceId(bioTaskDtlTb.getInstanceId() != null ? String.valueOf(bioTaskDtlTb.getInstanceId()) : null);
        if (StringUtils.isNotEmpty(bioTaskDetailRspDTO.getRefTaskNum())) {
            BioTaskDtlTb refBioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(bioTaskDetailRspDTO.getRefTaskNum());
            if (refBioTaskDtlTb != null) {
                bioTaskDetailRspDTO.setRefTaskDesc(refBioTaskDtlTb.getTaskDesc());
            }
        }
        return bioTaskDetailRspDTO;
    }


    @Override
    public PageInfo<BioTaskListPageRspDTO> listPage(BioTaskListPageReqDTO bioTaskListPageReqDTO, QueryTypeEnum queryTypeEnum) {

        PageHelper.startPage(bioTaskListPageReqDTO.getPageNum(), bioTaskListPageReqDTO.getPageSize());
        List<BioTaskDtlTb> bioTaskDtlTbList = new ArrayList<>();
        if (QueryTypeEnum.TYPE_1 == queryTypeEnum) {
            bioTaskDtlTbList = bioTaskDtlTbMapper.selectSelective(BioTaskDtlTb.builder().taskNum(bioTaskListPageReqDTO.getTaskNum()).taskTypeCode(bioTaskListPageReqDTO.getTaskTypeCode()).taskCategory(bioTaskListPageReqDTO.getTaskCategory()).build());
        } else if (QueryTypeEnum.TYPE_2 == queryTypeEnum) {
            bioTaskDtlTbList = bioTaskDtlTbMapper.selectForPendingApproval(String.valueOf(SecurityContextHolder.getUserId()), bioTaskListPageReqDTO.getTaskNum(), bioTaskListPageReqDTO.getTaskTypeCode(), bioTaskListPageReqDTO.getTaskCategory());
        } else if (QueryTypeEnum.TYPE_3 == queryTypeEnum) {
            bioTaskDtlTbList = bioTaskDtlTbMapper.selectSelective(BioTaskDtlTb.builder().taskNum(bioTaskListPageReqDTO.getTaskNum()).taskTypeCode(bioTaskListPageReqDTO.getTaskTypeCode()).applyUserId(SecurityContextHolder.getUserId()).taskCategory(bioTaskListPageReqDTO.getTaskCategory()).build());
        } else if (QueryTypeEnum.TYPE_4 == queryTypeEnum) {
            bioTaskDtlTbList = bioTaskDtlTbMapper.selectForAlreadyApproval(String.valueOf(SecurityContextHolder.getUserId()), bioTaskListPageReqDTO.getTaskNum(), bioTaskListPageReqDTO.getTaskTypeCode(), bioTaskListPageReqDTO.getTaskCategory());
        }
        PageInfo<BioTaskDtlTb> pageInfo = new PageInfo<>(bioTaskDtlTbList);
        List<BioTaskListPageRspDTO> bioTaskListPageRspDTOList = getTaskListPageRspDTOS(bioTaskDtlTbList);
        PageInfo<BioTaskListPageRspDTO> pageResult = new PageInfo<>();
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setList(bioTaskListPageRspDTOList);
        return pageResult;
    }

    private List<BioTaskListPageRspDTO> getTaskListPageRspDTOS(List<BioTaskDtlTb> bioTaskDtlTbList) {
        ResponseResult<List<UserBaseInfoRspDTO>> responseResult = remoteUserService.list();
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        List<BioTaskListPageRspDTO> bioTaskListPageRspDTOList = new ArrayList<>();
        for (BioTaskDtlTb bioTaskDtlTb : bioTaskDtlTbList) {
            BioTaskListPageRspDTO bioTaskListPageRspDTO = new BioTaskListPageRspDTO();
            bioTaskListPageRspDTO.setId(bioTaskDtlTb.getId());
            bioTaskListPageRspDTO.setTaskTypeCode(bioTaskDtlTb.getTaskTypeCode());
            bioTaskListPageRspDTO.setTaskTypeName(bioTaskDtlTb.getTaskTypeName());
            bioTaskListPageRspDTO.setTaskNum(bioTaskDtlTb.getTaskNum());
            bioTaskListPageRspDTO.setTaskStatus(bioTaskDtlTb.getTaskStatus());
            bioTaskListPageRspDTO.setTaskDesc(bioTaskDtlTb.getTaskDesc());
            bioTaskListPageRspDTO.setApplyUserId(bioTaskDtlTb.getApplyUserId());
            bioTaskListPageRspDTO.setApplyUserName(bioTaskDtlTb.getApplyUserName());
            bioTaskListPageRspDTO.setApplyTime(bioTaskDtlTb.getApplyTime());
            bioTaskListPageRspDTO.setProjectDesc(bioTaskDtlTb.getTaskDesc());
            bioTaskListPageRspDTO.setTaskForm(bioTaskDtlTb.getTaskForm());
            bioTaskListPageRspDTO.setInstanceId(String.valueOf(bioTaskDtlTb.getInstanceId()));
            bioTaskListPageRspDTOList.add(bioTaskListPageRspDTO);
        }
        return bioTaskListPageRspDTOList;
    }

    @Override
    public List<BioTaskTypeListRspDTO> listAllTaskType(String category) {
        List<BioTaskTypeListRspDTO> resultList = new ArrayList<>();
        List<BioTaskConf> bioTaskConfList = bioTaskConfMapper.selectAllByTaskCategory(category);
        for (BioTaskConf bioTaskConf : bioTaskConfList) {
            if (bioTaskConf.getProcessId() == null) {
                continue;
            }
            if (flowService.queryCanApplyList(bioTaskConf.getProcessId()).contains(String.valueOf(SecurityContextHolder.getUserId()))) {
                BioTaskTypeListRspDTO bioTaskTypeListRspDTO = new BioTaskTypeListRspDTO();
                bioTaskTypeListRspDTO.setTaskTypeName(bioTaskConf.getTaskTypeName());
                bioTaskTypeListRspDTO.setTaskTypeCode(bioTaskConf.getTaskTypeCode());
                bioTaskTypeListRspDTO.setProcessId(String.valueOf(bioTaskConf.getProcessId()));
                bioTaskTypeListRspDTO.setTaskCategory(bioTaskConf.getTaskCategory());
                resultList.add(bioTaskTypeListRspDTO);
            }
        }
        return resultList;
    }

    @Override
    public List<QueryListRspDTO> queryList(QueryListReqDTO queryListReqDTO) {
        List<QueryListRspDTO> queryListRspDTOList = new ArrayList<>();
        List<BioTaskDtlTb> bioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByTaskTypeCodeAndApplyUserIdOrderByIdDesc(queryListReqDTO.getTaskTypeCode(), SecurityContextHolder.getUserId());
        for (BioTaskDtlTb bioTaskDtlTb : bioTaskDtlTbList) {
            QueryListRspDTO queryListRspDTO = new QueryListRspDTO();
            queryListRspDTO.setId(bioTaskDtlTb.getId());
            queryListRspDTO.setTaskTypeCode(bioTaskDtlTb.getTaskTypeCode());
            queryListRspDTO.setTaskTypeName(bioTaskDtlTb.getTaskTypeName());
            queryListRspDTO.setTaskNum(bioTaskDtlTb.getTaskNum());
            queryListRspDTO.setTaskStatus(bioTaskDtlTb.getTaskStatus());
            queryListRspDTO.setTaskDesc(bioTaskDtlTb.getTaskDesc());
            queryListRspDTO.setApplyUserId(bioTaskDtlTb.getApplyUserId());
            queryListRspDTO.setApplyUserName(bioTaskDtlTb.getApplyUserName());
            queryListRspDTO.setApplyTime(bioTaskDtlTb.getApplyTime());
            queryListRspDTO.setTaskForm(bioTaskDtlTb.getTaskForm());
            queryListRspDTO.setInstanceId(bioTaskDtlTb.getInstanceId() + "");
            queryListRspDTOList.add(queryListRspDTO);

        }
        return queryListRspDTOList;
    }


    private BioTaskDtlTb initTask(BioTaskStartReqDTO bioTaskStartReqDTO, BioTaskConf bioTaskConf) {
        log.info("【任务工单】 初始化任务开始");
        BioTaskDtlTb bioTaskDtlTb = new BioTaskDtlTb();
        bioTaskDtlTb.setTaskTypeCode(bioTaskStartReqDTO.getTaskType());
        bioTaskDtlTb.setTaskTypeName(bioTaskConf.getTaskTypeName());
        bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_1.status);
        bioTaskDtlTb.setTaskDesc(bioTaskStartReqDTO.getTaskDesc());
        bioTaskDtlTb.setApplyUserId(SecurityContextHolder.getUserId());
        bioTaskDtlTb.setApplyUserName(SecurityContextHolder.getNickName());
        bioTaskDtlTb.setApplyTime(new Date());
        bioTaskDtlTb.setCreateTime(new Date());
        bioTaskDtlTb.setUpdateTime(null);
        bioTaskDtlTb.setTaskForm(bioTaskStartReqDTO.getFormObject());
        bioTaskDtlTb.setRefTaskNum(bioTaskStartReqDTO.getRefTaskNum());
        bioTaskDtlTb.setTaskCategory(bioTaskConf.getTaskCategory());
        bioTaskDtlTbMapper.insert(bioTaskDtlTb);
        bioTaskDtlTb.setTaskNum(bioTaskConf.getBeginLetter() + StringUtils.padl(bioTaskDtlTb.getId() + "", 7, '0'));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        log.info("【任务工单】 初始化任务结束");
        return bioTaskDtlTb;
    }


    @Override
    public PageInfo<SeedInStoreDTO.ExecuteFormContent> seedInData(SeedInDataReqDTO seedInDataReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(seedInDataReqDTO.getId());
        SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedInStoreDTO.class);
        List<SeedInStoreDTO.ExecuteFormContent> executeFormContentList = seedInStoreDTO.getExecuteForm().getExecuteFormContentList();
        PaginationHelper paginationHelper = new PaginationHelper(executeFormContentList, seedInDataReqDTO.getPageNum(), seedInDataReqDTO.getPageSize());
        executeFormContentList = paginationHelper.getCurrentPageData();
        PageInfo<SeedInStoreDTO.ExecuteFormContent> pageInfo = new PageInfo<>();
        pageInfo.setList(BeanUtils.copyToList(executeFormContentList, SeedInStoreDTO.ExecuteFormContent.class));
        pageInfo.setTotal(paginationHelper.getTotalNum());
        return pageInfo;
    }


    @Override
    public List<SeedTaskSeedNumRspDTO> findAllSeedNum(Integer id) {
        List<SeedTaskSeedNumRspDTO> result = new ArrayList<>();
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(id);
        if (SeedTaskTypeEnum.seed_out_apply.name().equals(bioTaskDtlTb.getTaskTypeCode())) {
            SeedOutDTO seedOutDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedOutDTO.class);
            for (SeedOutDTO.ExecuteFormContent executeFormContent : seedOutDTO.getExecuteForm().getExecuteFormContentList()) {
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(executeFormContent.getSeedNum());
                result.add(new SeedTaskSeedNumRspDTO(seedStockTb.getSeedNum(), seedStockTb.getUnit(), null, executeFormContent.getNum()));
            }
        } else if (SeedTaskTypeEnum.seed_store_apply.name().equals(bioTaskDtlTb.getTaskTypeCode())) {
            List<SeedStockInLog> seedStockInLogList = seedStockInLogMapper.selectAllByTaskNum(bioTaskDtlTb.getTaskNum());
            for (SeedStockInLog seedStockInLog : seedStockInLogList) {
                result.add(new SeedTaskSeedNumRspDTO(seedStockInLog.getSeedNum(), seedStockInLog.getUnit(), seedStockInLog.getId(), seedStockInLog.getSeedNumber().toString()));
            }
        }
        return result;
    }


    @Override
    public void temporarySave(BioTaskTemporarySaveReqDTO bioTaskTemporarySaveReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioTaskTemporarySaveReqDTO.getId());
        if (bioTaskDtlTb != null) {
            bioTaskDtlTb.setTaskForm(bioTaskTemporarySaveReqDTO.getFormObject());
        }
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
    }


    private void afterFLow(BioTaskDtlTb bioTaskDtlTb, FlowHisInstanceTb flowHisInstanceTb) {
        bioTaskDtlTb.setUpdateTime(new Date());
        BaseTaskService baseTaskService = SpringUtils.getBean(bioTaskDtlTb.getTaskTypeCode());
        /**
         * 任务完成
         */
        if (InstanceState.completed.getValue().intValue() == flowHisInstanceTb.getInstanceState().intValue()) {

            bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_2.status);

            baseTaskService.executeTask(bioTaskDtlTb);

            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

            cerProjectTaskListener.notice(EventType.complete, () -> bioTaskDtlTb);



        } else if (InstanceState.reject.getValue().intValue() == flowHisInstanceTb.getInstanceState().intValue()
                || InstanceState.timeout.getValue().intValue() == flowHisInstanceTb.getInstanceState().intValue()) {
            /**
             * 任务拒绝或者超时
             */
            bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_3.status);

            baseTaskService.cancelTask(bioTaskDtlTb);

            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

            cerProjectTaskListener.notice(EventType.reject, () -> bioTaskDtlTb);

        } else if (InstanceState.revoke.getValue().intValue() == flowHisInstanceTb.getInstanceState().intValue()) {
            /**
             * 任务撤销
             */
            bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_4.status);

            baseTaskService.cancelTask(bioTaskDtlTb);

            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

            cerProjectTaskListener.notice(EventType.revoke, () -> bioTaskDtlTb);

        } else {
            /**
             * 任务执行中
             */
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

            cerProjectTaskListener.notice(EventType.active, () -> bioTaskDtlTb);
        }
    }
}
