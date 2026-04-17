package com.bio.drqi.manage.flowtask.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.domain.CerSubProjectTb;
import com.bio.drqi.manage.dto.project.SubProjectCreateDTO;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.bio.drqi.mapper.CerSubProjectTbMapper;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service("sub_project_create")
@Slf4j
public class SubProjectProcServiceBase extends AbstractProjectBaseTaskService {

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        log.info("【任务工单】子项目开始创建");
        SubProjectCreateDTO subProjectCreateDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SubProjectCreateDTO.class);
        if (subProjectCreateDTO == null) {
            throw new BusinessException("工单关联的子项目创建表单缺失");
        }
        ValidatorUtil.validator(subProjectCreateDTO);

        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(subProjectCreateDTO.getProjectId());
        if (cerProjectTb == null) {
            throw new BusinessException("未找到项目信息 projectId=" + subProjectCreateDTO.getProjectId());
        }
        if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("非执行中项目不能进行该操作");
        }
        //子项目编号校验
        checkSubProjectCode(subProjectCreateDTO);

        List<CerSubProjectTb> cerSubProjectTbList = cerSubProjectTbMapper.selectAllByTaskNum(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isEmpty(cerSubProjectTbList)) {
            synchronized (this) {
                for (SubProjectCreateDTO.Content content : subProjectCreateDTO.getContentList()) {
                    CerSubProjectTb cerSubProjectTb = new CerSubProjectTb();
                    cerSubProjectTb.setProjectId(subProjectCreateDTO.getProjectId());
                    cerSubProjectTb.setSubProjectCode(content.getSubProjectCode());
                    cerSubProjectTb.setCreateTime(new Date());
                    cerSubProjectTb.setUpdateTime(new Date());
                    cerSubProjectTb.setFileUrls(JSONUtil.toJsonStr(content.getFileUrls()));
                    cerSubProjectTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                    cerSubProjectTb.setCreateUserName(SecurityContextHolder.getNickName());
                    cerSubProjectTb.setCreateUserId(SecurityContextHolder.getUserId());
                    cerSubProjectTb.setPriorityLevel(content.getPriorityLevel());
                    cerSubProjectTb.setTaskStatus(bioTaskDtlTb.getTaskStatus());
                    cerSubProjectTb.setSpeciesCode(JSONUtil.toJsonStr(content.getSpeciesList()));
                    cerSubProjectTb.setProjectCode(cerProjectTb.getProjectCode());
                    try {
                        cerSubProjectTbMapper.insert(cerSubProjectTb);
                    } catch (DuplicateKeyException e) {
                        throw new BusinessException("子项目编号重复：" + cerSubProjectTb.getSubProjectCode());
                    }
                }
            }
        }
        subProjectCreateDTO.setProjectName(cerProjectTb.getProjectName());
        subProjectCreateDTO.setProjectCode(cerProjectTb.getProjectCode());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(subProjectCreateDTO));

        log.info("【任务工单】子项目创建完毕");
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            cerSubProjectTbMapper.updateTaskStatusByTaskNum(BioTaskStatusEnum.TASK_STATUS_2.status, bioTaskDtlTb.getTaskNum());
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        cerSubProjectTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
    }

    private void checkSubProjectCode(SubProjectCreateDTO subProjectCreateDTO) {
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(subProjectCreateDTO.getProjectId());
        for (SubProjectCreateDTO.Content content : subProjectCreateDTO.getContentList()) {
            if (!content.getSubProjectCode().startsWith(cerProjectTb.getProjectCode())) {
                throw new BusinessException("子项目编号必须以项目编号开头：" + content.getSubProjectCode());
            }
            if (!content.getSubProjectCode().matches("^[A-Za-z0-9]+$")) {
                throw new BusinessException("子项目编号只能输数字或者字符");
            }

        }
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        return Collections.emptyList();
    }
}
