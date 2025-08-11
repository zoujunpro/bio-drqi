package com.bio.flow.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserBaseInfoRspDTO;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.SpringUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.BioTaskConf;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.mapper.BioTaskConfMapper;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.flow.dto.*;
import com.bio.flow.enums.EventType;
import com.bio.flow.enums.QueryTypeEnum;
import com.easyflow.engine.entity.FlowHisInstanceTb;
import com.easyflow.engine.entity.FlowTaskTb;
import com.easyflow.engine.enums.InstanceState;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW, timeout = 120000)
    public BioTaskDtlTb start(BioTaskStartReqDTO bioTaskStartReqDTO) {
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
        baseTaskService.taskApply(bioTaskDtlTb);
        /**
         * 执行工作流
         */
        FlowHisInstanceTb flowHisInstanceTb = flowService.start(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskConf.getProcessId(), flowService.getArgs(bioTaskStartReqDTO.getFormObject(), bioTaskStartReqDTO.getTaskType()), null, bioTaskStartReqDTO.getSelfFlowActorList(), bioTaskConf.getTaskTypeName(), bioTaskDtlTb.getTaskNum());

        /**
         * 填补数据
         */
        bioTaskDtlTb.setInstanceId(flowHisInstanceTb.getId());
        bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_1.status);
        /**
         * 流程执行后判断流程状态
         */
        afterFLow(bioTaskDtlTb, flowHisInstanceTb);

        bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioTaskDtlTb.getId());
        return bioTaskDtlTb;

    }


    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 120000)
    public BioTaskDtlTb reStartTask(BioReStartTaskReqDTO bioReStartTaskReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioReStartTaskReqDTO.getId());
        Assert.notNull(bioTaskDtlTb, "不存在此任务");
        if (BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("任务执行中，不能再次执行");
        }
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("任务已经执行完毕，不能再次执行");
        }
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
        baseTaskService.taskApply(bioTaskDtlTb);

        /**
         * 执行工作流
         */
        FlowHisInstanceTb flowHisInstanceTb = flowService.start(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskConf.getProcessId(), flowService.getArgs(bioReStartTaskReqDTO.getFormObject(), bioTaskDtlTb.getTaskTypeCode()), null, bioReStartTaskReqDTO.getSelfFlowActorList(), bioTaskConf.getTaskTypeName(), bioTaskDtlTb.getTaskNum());

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

        bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioReStartTaskReqDTO.getId());
        return bioTaskDtlTb;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 120000)
    public BioTaskDtlTb executeTask(BioExecuteTaskReqDTO bioExecuteTaskReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioExecuteTaskReqDTO.getId());
        Assert.notNull(bioTaskDtlTb, "不存在此任务");
        if (StringUtils.isNotEmpty(bioExecuteTaskReqDTO.getFormObject())) {
            bioTaskDtlTb.setTaskForm(bioExecuteTaskReqDTO.getFormObject());
        }
        BioTaskConf bioTaskConf = bioTaskConfMapper.selectOneByTaskTypeCode(bioTaskDtlTb.getTaskTypeCode());
        BaseTaskService baseTaskService = SpringUtils.getBean(bioTaskConf.getTaskTypeCode());
        baseTaskService.executeTask(bioTaskDtlTb);

        FlowHisInstanceTb flowHisInstanceTb = flowService.execute(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskDtlTb.getInstanceId(), flowService.getArgs(bioTaskDtlTb.getTaskForm(), bioTaskDtlTb.getTaskTypeCode()), null);

        /**
         * 流程执行后判断流程状态
         */
        afterFLow(bioTaskDtlTb, flowHisInstanceTb);

        bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioExecuteTaskReqDTO.getId());
        return bioTaskDtlTb;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 120000)
    public BioTaskDtlTb rejectTask(BioRejectTaskReqDTO bioRejectTaskReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioRejectTaskReqDTO.getId());
        Assert.notNull(bioTaskDtlTb, "不存在此任务");

        FlowHisInstanceTb flowHisInstanceTb = flowService.reject(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskDtlTb.getInstanceId(), null, bioRejectTaskReqDTO.getReason());
        /**
         * 流程执行后判断流程状态
         */
        afterFLow(bioTaskDtlTb, flowHisInstanceTb);

        bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioRejectTaskReqDTO.getId());
        return bioTaskDtlTb;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 120000)
    public BioTaskDtlTb backTask(BioBackTaskReqDTO bioRejectTaskReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioRejectTaskReqDTO.getId());
        Assert.notNull(bioTaskDtlTb, "不存在此任务");
        FlowHisInstanceTb flowHisInstanceTb = flowService.back(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskDtlTb.getInstanceId(), null, bioRejectTaskReqDTO.getReason());
        /**
         * 流程执行后判断流程状态
         */
        afterFLow(bioTaskDtlTb, flowHisInstanceTb);

        return bioTaskDtlTbMapper.selectById(bioRejectTaskReqDTO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 120000)
    public BioTaskDtlTb revokeTask(BioRevokeTaskReqDTO bioRevokeTaskReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioRevokeTaskReqDTO.getId());
        Assert.notNull(bioTaskDtlTb, "不存在此任务");
        if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
            throw new BusinessException("非执行任务无法撤销");
        }

        FlowHisInstanceTb flowHisInstanceTb = flowService.revoke(SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), bioTaskDtlTb.getInstanceId(), bioRevokeTaskReqDTO.getReason());
        /**
         * 流程执行后判断流程状态
         */
        afterFLow(bioTaskDtlTb, flowHisInstanceTb);

        bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioRevokeTaskReqDTO.getId());
        return bioTaskDtlTb;
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
    public BioTaskDetailRspDTO detailByTaskNum(String taskNum) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(taskNum);
        return detail(bioTaskDtlTb.getId());
    }


    @Override
    public PageInfo<BioTaskListPageRspDTO> listPage(BioTaskListPageReqDTO bioTaskListPageReqDTO, QueryTypeEnum queryTypeEnum) {

        PageHelper.startPage(bioTaskListPageReqDTO.getPageNum(), bioTaskListPageReqDTO.getPageSize());
        List<BioTaskDtlTb> bioTaskDtlTbList = new ArrayList<>();
        if (QueryTypeEnum.TYPE_1 == queryTypeEnum) {
            bioTaskDtlTbList = bioTaskDtlTbMapper.selectSelective(BioTaskDtlTb.builder().taskNum(bioTaskListPageReqDTO.getTaskNum()).taskTypeCode(bioTaskListPageReqDTO.getTaskTypeCode()).taskStatus(bioTaskListPageReqDTO.getTaskStatus()).applyUserId(bioTaskListPageReqDTO.getApplyUserId()).taskCategory(bioTaskListPageReqDTO.getTaskCategory()).build());
        } else if (QueryTypeEnum.TYPE_2 == queryTypeEnum) {
            bioTaskDtlTbList = bioTaskDtlTbMapper.selectForPendingApproval(String.valueOf(SecurityContextHolder.getUserId()), bioTaskListPageReqDTO.getTaskNum(), bioTaskListPageReqDTO.getTaskTypeCode(), bioTaskListPageReqDTO.getTaskCategory(), bioTaskListPageReqDTO.getApplyUserId());
        } else if (QueryTypeEnum.TYPE_3 == queryTypeEnum) {
            bioTaskDtlTbList = bioTaskDtlTbMapper.selectSelective(BioTaskDtlTb.builder().taskNum(bioTaskListPageReqDTO.getTaskNum()).taskStatus(bioTaskListPageReqDTO.getTaskStatus()).taskTypeCode(bioTaskListPageReqDTO.getTaskTypeCode()).applyUserId(SecurityContextHolder.getUserId()).taskCategory(bioTaskListPageReqDTO.getTaskCategory()).build());
        } else if (QueryTypeEnum.TYPE_4 == queryTypeEnum) {
            bioTaskDtlTbList = bioTaskDtlTbMapper.selectForAlreadyApproval(String.valueOf(SecurityContextHolder.getUserId()), bioTaskListPageReqDTO.getTaskNum(), bioTaskListPageReqDTO.getTaskTypeCode(), bioTaskListPageReqDTO.getTaskCategory(), bioTaskListPageReqDTO.getTaskStatus(), bioTaskListPageReqDTO.getApplyUserId());
        }
        PageInfo<BioTaskDtlTb> pageInfo = new PageInfo<>(bioTaskDtlTbList);
        List<BioTaskListPageRspDTO> bioTaskListPageRspDTOList = getTaskListPageRspDTOS(bioTaskDtlTbList);
        List<Long> instanceIdList = bioTaskListPageRspDTOList.stream().filter(bioTaskListPageRspDTO -> bioTaskListPageRspDTO.getInstanceId() != null).map(bioTaskListPageRspDTO -> Long.valueOf(bioTaskListPageRspDTO.getInstanceId())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(instanceIdList)){
            Map<Long, String> instanceTaskTypeMap = flowService.queryListFlowTaskByInstanceIds(instanceIdList);
            bioTaskListPageRspDTOList.forEach(bioTaskListPageRspDTO -> {
                bioTaskListPageRspDTO.setNodeType(instanceTaskTypeMap.get(Long.valueOf(bioTaskListPageRspDTO.getInstanceId())));
            });
        }
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
            bioTaskListPageRspDTO.setInstanceId(bioTaskDtlTb.getInstanceId() == null ? null : String.valueOf(bioTaskDtlTb.getInstanceId()));
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
    public BioTaskTypeListRspDTO listOneTaskType(String taskTypeCode) {
        BioTaskConf bioTaskConf = bioTaskConfMapper.selectOneByTaskTypeCode(taskTypeCode);
        if (bioTaskConf == null) {
            throw new BusinessException("暂未配置此流程，请联系开发人员配置");
        }
        if (!flowService.queryCanApplyList(bioTaskConf.getProcessId()).contains(String.valueOf(SecurityContextHolder.getUserId()))) {
            throw new BusinessException("该流程未给您配置访问权限，如果需要，请联系部门负责人");
        }
        BioTaskTypeListRspDTO bioTaskTypeListRspDTO = new BioTaskTypeListRspDTO();
        bioTaskTypeListRspDTO.setTaskTypeName(bioTaskConf.getTaskTypeName());
        bioTaskTypeListRspDTO.setTaskTypeCode(bioTaskConf.getTaskTypeCode());
        bioTaskTypeListRspDTO.setProcessId(String.valueOf(bioTaskConf.getProcessId()));
        bioTaskTypeListRspDTO.setTaskCategory(bioTaskConf.getTaskCategory());
        return bioTaskTypeListRspDTO;
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


    private BioTaskDtlTb initTempTask(BioTaskTemporarySaveReqDTO bioTaskTemporarySaveReqDTO, BioTaskConf bioTaskConf) {
        log.info("【任务工单】 草稿初始化任务开始");
        BioTaskDtlTb bioTaskDtlTb = new BioTaskDtlTb();
        bioTaskDtlTb.setTaskTypeCode(bioTaskTemporarySaveReqDTO.getTaskType());
        bioTaskDtlTb.setTaskTypeName(bioTaskConf.getTaskTypeName());
        bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_0.status);
        bioTaskDtlTb.setTaskDesc(bioTaskTemporarySaveReqDTO.getTaskDesc());
        bioTaskDtlTb.setApplyUserId(SecurityContextHolder.getUserId());
        bioTaskDtlTb.setApplyUserName(SecurityContextHolder.getNickName());
        bioTaskDtlTb.setApplyTime(new Date());
        bioTaskDtlTb.setCreateTime(new Date());
        bioTaskDtlTb.setUpdateTime(null);
        bioTaskDtlTb.setTaskForm(bioTaskTemporarySaveReqDTO.getFormObject());
        bioTaskDtlTb.setRefTaskNum(bioTaskTemporarySaveReqDTO.getRefTaskNum());
        bioTaskDtlTb.setTaskCategory(bioTaskConf.getTaskCategory());
        bioTaskDtlTbMapper.insert(bioTaskDtlTb);
        bioTaskDtlTb.setTaskNum(bioTaskConf.getBeginLetter() + StringUtils.padl(bioTaskDtlTb.getId() + "", 7, '0'));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        log.info("【任务工单】 草稿初始化任务结束");
        return bioTaskDtlTb;
    }

    private BioTaskDtlTb initTask(BioTaskStartReqDTO bioTaskStartReqDTO, BioTaskConf bioTaskConf) {
        if (bioTaskStartReqDTO.getId() != null) {
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioTaskStartReqDTO.getId());
            if (bioTaskDtlTb == null) {
                throw new BusinessException("数据异常，无此任务");
            }
            if (BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
                throw new BusinessException("任务执行中，不能再次执行");
            }
            if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
                throw new BusinessException("任务已经执行完毕，不能再次执行");
            }
            bioTaskDtlTb.setTaskForm(bioTaskStartReqDTO.getFormObject());
            bioTaskDtlTb.setRefTaskNum(bioTaskStartReqDTO.getRefTaskNum());
            bioTaskDtlTb.setTaskCategory(bioTaskConf.getTaskCategory());
            bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_1.status);
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
            return bioTaskDtlTb;
        } else {
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

    }


    @Override

    public void temporarySave(BioTaskTemporarySaveReqDTO bioTaskTemporarySaveReqDTO) {
        if (bioTaskTemporarySaveReqDTO.getId() != null) {
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectById(bioTaskTemporarySaveReqDTO.getId());
            if (bioTaskDtlTb == null) {
                throw new BusinessException("数据异常，保存失败，找不到工单信息ID=" + bioTaskTemporarySaveReqDTO.getId());
            }
            bioTaskDtlTb.setTaskForm(bioTaskTemporarySaveReqDTO.getFormObject());
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        } else {
            BioTaskConf bioTaskConf = bioTaskConfMapper.selectOneByTaskTypeCode(bioTaskTemporarySaveReqDTO.getTaskType());
            if (bioTaskConf == null) {
                throw new BusinessException("任务类型参数错误");
            }
            /**
             * 初始化草稿任务
             */
            initTempTask(bioTaskTemporarySaveReqDTO, bioTaskConf);
        }
    }

    @Override
    public List<BioQueryAllTaskUserRspDTO> queryAllTaskUser(String taskCategory) {
        List<BioQueryAllTaskUserRspDTO> result = new ArrayList<>();
        List<BioTaskDtlTb> bioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByTaskCategory(taskCategory);
        if (CollectionUtil.isNotEmpty(bioTaskDtlTbList)) {
            bioTaskDtlTbList.forEach(bioTaskDtlTb -> {
                BioQueryAllTaskUserRspDTO bioQueryAllTaskUserRspDTO = new BioQueryAllTaskUserRspDTO();
                bioQueryAllTaskUserRspDTO.setApplyUserName(bioTaskDtlTb.getApplyUserName());
                bioQueryAllTaskUserRspDTO.setApplyUserId(bioTaskDtlTb.getApplyUserId());
                result.add(bioQueryAllTaskUserRspDTO);
            });


        }
        return result;
    }

    @Override
    public void exportExcel(BioExportExcelReqDTO bioExportExcelReqDTO, HttpServletResponse httpServletResponse) {
        List<BioTaskDtlTb> bioTaskDtlTbList = bioTaskDtlTbMapper.selectSelective(BioTaskDtlTb.builder().taskNum(bioExportExcelReqDTO.getTaskNum()).taskTypeCode(bioExportExcelReqDTO.getTaskTypeCode()).taskStatus(bioExportExcelReqDTO.getTaskStatus()).applyUserId(bioExportExcelReqDTO.getApplyUserId()).taskCategory(bioExportExcelReqDTO.getTaskCategory()).build());
        List<BioTaskExcelDTO> bioTaskExcelDTOList = BeanUtils.copyToList(bioTaskDtlTbList, BioTaskExcelDTO.class);
        bioTaskExcelDTOList.forEach(bioTaskExcelDTO -> {
            bioTaskExcelDTO.setTaskStatusName(BioTaskStatusEnum.getNameByStatus(bioTaskExcelDTO.getTaskStatus()));
        });
        ExcelUtil.writeExcel("任务工单" + System.currentTimeMillis() + ".xlsx", "sheet1", bioTaskExcelDTOList, BioTaskExcelDTO.class, httpServletResponse);
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

            bioTaskNotice(bioTaskDtlTb, EventType.complete);


        } else if (InstanceState.reject.getValue().intValue() == flowHisInstanceTb.getInstanceState().intValue()
                || InstanceState.timeout.getValue().intValue() == flowHisInstanceTb.getInstanceState().intValue()) {
            /**
             * 任务拒绝或者超时
             */
            bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_3.status);

            baseTaskService.cancelTask(bioTaskDtlTb);

            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

            bioTaskNotice(bioTaskDtlTb, EventType.reject);


        } else if (InstanceState.revoke.getValue().intValue() == flowHisInstanceTb.getInstanceState().intValue()) {
            /**
             * 任务撤销
             */
            bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_4.status);

            baseTaskService.cancelTask(bioTaskDtlTb);

            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

            bioTaskNotice(bioTaskDtlTb, EventType.revoke);

        } else {
            /**
             * 任务执行中
             */

            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

            bioTaskNotice(bioTaskDtlTb, EventType.active);

        }
    }

    private void bioTaskNotice(BioTaskDtlTb bioTaskDtlTb, EventType eventType) {
        Map<String, FlowTaskListener> cerTaskListenerMap = SpringUtil.getBeansOfType(FlowTaskListener.class);
        for (FlowTaskListener cerTaskListener : cerTaskListenerMap.values()) {
            cerTaskListener.notice(eventType, () -> bioTaskDtlTb);
        }
    }
}
