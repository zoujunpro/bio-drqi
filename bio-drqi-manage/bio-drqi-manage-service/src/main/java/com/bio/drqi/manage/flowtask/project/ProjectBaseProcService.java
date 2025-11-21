package com.bio.drqi.manage.flowtask.project;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;

import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.manage.dto.project.ProjectAddDTO;
import com.bio.drqi.mapper.CerProjectTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service("project_create")
@Slf4j
public class ProjectBaseProcService extends AbstractProjectBaseTaskService {

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;


    @Override
    public void taskApply(@NotNull BioTaskDtlTb bioTaskDtlTb) {
        ProjectAddDTO projectAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ProjectAddDTO.class);
        ValidatorUtil.validator(projectAddDTO);
        BeanUtils.trimFiledSpace(projectAddDTO);

        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(projectAddDTO.getProjectCode());
        if (cerProjectTb != null) {
            throw new BusinessException("项目编号已经使用");
        }
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            log.info("【任务工单】项目立项开始进行入库操作");
            ProjectAddDTO projectAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ProjectAddDTO.class);
            ValidatorUtil.validator(projectAddDTO);
            BeanUtils.trimFiledSpace(projectAddDTO);
            //项目数据初始化
            CerProjectTb cerProjectTb = new CerProjectTb();
            BeanUtil.copyProperties(projectAddDTO, cerProjectTb);
            cerProjectTb.setCreateTime(new Date());
            cerProjectTb.setUpdateTime(new Date());
            cerProjectTb.setOwnerUserId(bioTaskDtlTb.getApplyUserId());
            cerProjectTb.setOwnerUserName(bioTaskDtlTb.getApplyUserName());
            cerProjectTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
            cerProjectTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
            cerProjectTb.setProjectStatus(ProjectStatusEnum.execute.name());
            cerProjectTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            cerProjectTb.setProjectCategoryCode(projectAddDTO.getProjectCategoryCode());
            cerProjectTb.setGeneEditMethod(projectAddDTO.getGeneEditMethod());
            cerProjectTb.setProjectType(projectAddDTO.getProjectType());
            cerProjectTb.setProjectCode(projectAddDTO.getProjectCode());
            cerProjectTb.setProjectName(projectAddDTO.getProjectName());
            cerProjectTb.setExpectStartDate(projectAddDTO.getExpectStartDate());
            try {
                cerProjectTbMapper.insert(cerProjectTb);
            } catch (DuplicateKeyException e) {
                throw new BusinessException("项目名称或者编号已经存在");
            }
            log.info("【任务工单】项目立项 入库操作完成");
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
        if (cerProjectTb != null) {
            cerProjectTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());

        }
    }
}
