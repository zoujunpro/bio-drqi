package com.bio.drqi.manage.service.task.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.enums.QualityInspectionResultEnum;
import com.bio.drqi.manage.dto.project.ImplementPlanAddDTO;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实验方案创建
 */
@Service("implementation_plan")
@Slf4j
public class ImplementationPlanProcServiceBase extends AbstractProjectBaseTaskService {

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

    @Resource
    private CerSampleCodePrefixTbMapper cerSampleCodePrefixTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        log.info("【任务工单】实施方案构建校验开始");
        ImplementPlanAddDTO implementPlanAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ImplementPlanAddDTO.class);
        ValidatorUtil.validator(implementPlanAddDTO);
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectById(implementPlanAddDTO.getSubProjectId());
        if (cerSubProjectTb == null) {
            throw new BusinessException("未找到子项目信息");
        }
        if (!cerSubProjectTb.getProjectId().equals(implementPlanAddDTO.getProjectId())) {
            throw new BusinessException("入参非法，子项目归属错误");
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(implementPlanAddDTO.getProjectId());
        if (cerProjectTb == null) {
            throw new BusinessException("未找到项目信息 projectId=" + implementPlanAddDTO.getProjectId());
        }
        if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("非执行中项目不能进行该操作");
        }
        if (!implementPlanAddDTO.getVectorTaskCode().startsWith(cerSubProjectTb.getSubProjectCode())) {
            throw new BusinessException("任务编号必须以子项目编号开头");
        }
        if (implementPlanAddDTO.getVectorTaskCode().split("-").length != 2) {
            throw new BusinessException("任务编号非法");
        }

        synchronized (this) {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(bioTaskDtlTb.getTaskNum());
            if (cerVectorTaskTb == null) {
                synchronized (this) {
                    cerVectorTaskTb = new CerVectorTaskTb();
                    cerVectorTaskTb.setVectorTaskCode(implementPlanAddDTO.getVectorTaskCode());
                    cerVectorTaskTb.setDeliveryMethod(implementPlanAddDTO.getDeliveryMethod());
                    cerVectorTaskTb.setAcceptorMaterial(implementPlanAddDTO.getAcceptorMaterial());
                    cerVectorTaskTb.setCreateTime(new Date());
                    cerVectorTaskTb.setUpdateTime(new Date());
                    cerVectorTaskTb.setCreateUserId(SecurityContextHolder.getUserId());
                    cerVectorTaskTb.setCreateUserName(SecurityContextHolder.getNickName());
                    cerVectorTaskTb.setProjectId(cerProjectTb.getId());
                    cerVectorTaskTb.setSubProjectId(cerSubProjectTb.getId());
                    cerVectorTaskTb.setProjectCode(cerProjectTb.getProjectCode());
                    cerVectorTaskTb.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
                    cerVectorTaskTb.setEditType(implementPlanAddDTO.getEditType());
                    cerVectorTaskTb.setTaskStatus(bioTaskDtlTb.getTaskStatus());
                    cerVectorTaskTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                    cerVectorTaskTb.setSpeciesCode(implementPlanAddDTO.getSpeciesCode());
                    cerVectorTaskTb.setBreedCode(implementPlanAddDTO.getBreedCode());
                    cerVectorTaskTb.setExpectStartDate(implementPlanAddDTO.getExpectStartDate());
                    cerVectorTaskTb.setSupervisionLevelCode(implementPlanAddDTO.getSupervisionLevelCode());
                    cerVectorTaskTb.setExpectedPositiveSeed(implementPlanAddDTO.getExpectedPositiveSeed());
                    cerVectorTaskTb.setNoPlasmidFlag(implementPlanAddDTO.getNoPlasmidFlag());
                    cerVectorTaskTb.setExpectPeriod(implementPlanAddDTO.getExpectPeriod());
                    try {
                        cerVectorTaskTbMapper.insert(cerVectorTaskTb);
                    } catch (DuplicateKeyException e) {
                        throw new BusinessException("任务编号重复：" + cerVectorTaskTb.getVectorTaskCode());
                    }
                    CerSampleCodePrefixTb cerSampleCodePrefixTb = cerSampleCodePrefixTbMapper.selectOneByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                    if (cerSampleCodePrefixTb == null) {
                        //生成sampleCodePrefix
                        cerSampleCodePrefixTb = new CerSampleCodePrefixTb();
                        cerSampleCodePrefixTb.setSampleCodePrefix(createSampleCode());
                        cerSampleCodePrefixTb.setVectorTaskCode(implementPlanAddDTO.getVectorTaskCode());
                        cerSampleCodePrefixTb.setCreateTime(new Date());
                        cerSampleCodePrefixTb.setCurrentIndex(1);
                        try {
                            cerSampleCodePrefixTbMapper.insert(cerSampleCodePrefixTb);
                        } catch (DuplicateKeyException e) {
                            throw new BusinessException("取样编号前缀重复：" + cerSampleCodePrefixTb.getSampleCodePrefix());
                        }
                    }
                    implementPlanAddDTO.setSampleCodePrefix(cerSampleCodePrefixTb.getSampleCodePrefix());

                }
            }
        }
        //回填工单信息
        implementPlanAddDTO.setProjectCode(cerProjectTb.getProjectCode());
        implementPlanAddDTO.setProjectName(cerProjectTb.getProjectName());
        implementPlanAddDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(implementPlanAddDTO));
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
        cerSampleCodePrefixTbMapper.deleteByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
    }

    private String createSampleCode() {
        String sampleCodePrefix = LetterUtil.randomLetter(2);
        List<CerSampleCodePrefixTb> cerSampleCodePrefixTbList = cerSampleCodePrefixTbMapper.selectList(null);
        List<String> sampleCodePrefixList = cerSampleCodePrefixTbList.stream().map(CerSampleCodePrefixTb::getSampleCodePrefix).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(sampleCodePrefixList)) {
            return sampleCodePrefix;
        }
        while (sampleCodePrefixList.contains(sampleCodePrefix)) {
            sampleCodePrefix = LetterUtil.randomLetter(2);
        }
        return sampleCodePrefix;
    }

}
