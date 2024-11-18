package com.bio.drqi.manage.service.task.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.enums.QualityInspectionResultEnum;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.domain.CerSubProjectTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 实验方案创建
 */
@Service("implementation_plan")
@Slf4j
public class ImplementationPlanProcService extends AbstractBaseProjectTaskService {

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;

    @Override
    public void taskCheck(BioTaskDtlTb bioTaskDtlTb) {
        log.info("【任务工单】实施方案构建校验开始");
        VectorTaskAddDTO vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
        ValidatorUtil.validator(vectorTaskAddDTO);
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectById(vectorTaskAddDTO.getSubProjectId());
        if (cerSubProjectTb == null) {
            throw new BusinessException("未找到子项目信息");
        }
        if (!cerSubProjectTb.getProjectId().equals(vectorTaskAddDTO.getProjectId())) {
            throw new BusinessException("入参非法，子项目归属错误");
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(vectorTaskAddDTO.getProjectId());
        if (cerProjectTb == null) {
            throw new BusinessException("未找到项目信息 projectId=" + vectorTaskAddDTO.getProjectId());
        }
        if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("非执行中项目不能进行该操作");
        }
        if (!vectorTaskAddDTO.getVectorTaskCode().startsWith(cerSubProjectTb.getSubProjectCode())) {
            throw new BusinessException("任务编号必须以子项目编号开头");
        }
        if (vectorTaskAddDTO.getVectorTaskCode().split("-").length != 2) {
            throw new BusinessException("任务编号非法");
        }
        if (CollectionUtil.isEmpty(vectorTaskAddDTO.getVectorGroupList())) {
            throw new BusinessException("转化信息缺失");
        }
        synchronized (this) {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(bioTaskDtlTb.getTaskNum());
            if (cerVectorTaskTb == null) {
                synchronized (this) {
                    cerVectorTaskTb = new CerVectorTaskTb();
                    cerVectorTaskTb.setVectorTaskCode(vectorTaskAddDTO.getVectorTaskCode());
                    cerVectorTaskTb.setVectorTaskType(vectorTaskAddDTO.getVectorTaskType());
                    cerVectorTaskTb.setVectorTaskName(vectorTaskAddDTO.getVectorTaskName());
                    cerVectorTaskTb.setDeliveryMethod(vectorTaskAddDTO.getDeliveryMethod());
                    cerVectorTaskTb.setAcceptorMaterial(vectorTaskAddDTO.getAcceptorMaterial());
                    cerVectorTaskTb.setEditTools(vectorTaskAddDTO.getEditTools());
                    cerVectorTaskTb.setEditToolsType(vectorTaskAddDTO.getEditToolsType());
                    cerVectorTaskTb.setVectorTaskTarget(vectorTaskAddDTO.getVectorTaskTarget());
                    cerVectorTaskTb.setRemark(vectorTaskAddDTO.getRemark());
                    cerVectorTaskTb.setCreateTime(new Date());
                    cerVectorTaskTb.setUpdateTime(new Date());
                    cerVectorTaskTb.setCreateUserId(SecurityContextHolder.getUserId());
                    cerVectorTaskTb.setCreateUserName(SecurityContextHolder.getNickName());
                    cerVectorTaskTb.setProjectId(cerProjectTb.getId());
                    cerVectorTaskTb.setSubProjectId(cerSubProjectTb.getId());
                    cerVectorTaskTb.setProjectCode(cerProjectTb.getProjectCode());
                    cerVectorTaskTb.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
                    cerVectorTaskTb.setGeneEditMethod(cerProjectTb.getGeneEditMethod());
                    cerVectorTaskTb.setEditType(vectorTaskAddDTO.getEditType());
                    cerVectorTaskTb.setTaskStatus(bioTaskDtlTb.getTaskStatus());
                    cerVectorTaskTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                    cerVectorTaskTb.setWordUrl(vectorTaskAddDTO.getWordUrl());
                    cerVectorTaskTb.setSpeciesCode(vectorTaskAddDTO.getSpeciesCode());
                    cerVectorTaskTb.setExpectEndDate(vectorTaskAddDTO.getExpectEndDate());
                    cerVectorTaskTb.setExpectEndDate(vectorTaskAddDTO.getExpectEndDate());
                    cerVectorTaskTb.setQualityInspectionResult(QualityInspectionResultEnum.nocheck.name());
                    cerVectorTaskTb.setVectorBuildFlag(CerProjectContents.N);
                    try {
                        cerVectorTaskTbMapper.insert(cerVectorTaskTb);
                    } catch (DuplicateKeyException e) {
                        throw new BusinessException("任务编号重复：" + cerVectorTaskTb.getVectorTaskCode());
                    }
                }
            }
        }
        //回填工单信息
        vectorTaskAddDTO.setProjectCode(cerProjectTb.getProjectCode());
        vectorTaskAddDTO.setProjectName(cerProjectTb.getProjectName());
        vectorTaskAddDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        vectorTaskAddDTO.setSubProjectName(cerSubProjectTb.getSubProjectName());
        vectorTaskAddDTO.setGeneEditMethod(cerProjectTb.getGeneEditMethod());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(vectorTaskAddDTO));
        log.info("【任务工单】实施方案构建校验结束");
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            cerVectorTaskTbMapper.updateTaskStatusByTaskNum(BioTaskStatusEnum.TASK_STATUS_2.status, bioTaskDtlTb.getTaskNum());
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("数据异常，载体任务不存在");
        }
        cerVectorTaskTbMapper.deleteById(cerVectorTaskTb.getId());
        cerVectorTbMapper.deleteByVectorTaskId(cerVectorTaskTb.getId());
        cerVectorGroupTbMapper.deleteByVectorTaskId(cerVectorTaskTb.getId());
    }
}
