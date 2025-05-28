package com.bio.drqi.manage.service.task.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.enums.QualityInspectionResultEnum;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.PlasmidDTO;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("plasmid_check")
@Slf4j
public class PlasmidBaseProcService extends AbstractProjectBaseTaskService {
    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        log.info("【任务工单】质粒质检校验开始");
        PlasmidDTO plasmidDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
        ValidatorUtil.validator(plasmidDTO);
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(plasmidDTO.getVectorTaskId());

        if (!BioTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
            throw new BusinessException("任务审批中，不能质检");
        }
        if (QualityInspectionResultEnum.refuse.name().equals(cerVectorTaskTb.getQualityInspectionResult())) {
            throw new BusinessException("该实施方案质粒质检:通过");
        }
        if (QualityInspectionResultEnum.pass.name().equals(cerVectorTaskTb.getQualityInspectionResult())) {
            throw new BusinessException("该实施方案质粒质检:拒绝");
        }
        if (!CerProjectContents.Y.equals(cerVectorTaskTb.getVectorBuildFlag())) {
            throw new BusinessException("未进行载体构建");
        }
        if (QualityInspectionResultEnum.checking.name().equals(cerVectorTaskTb.getQualityInspectionResult())) {
            throw new BusinessException("已经提交过质粒质检");
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(cerVectorTaskTb.getProjectId());
        if (cerProjectTb == null) {
            throw new BusinessException("未找到项目信息 projectId=" + plasmidDTO.getProjectId());
        }
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectById(plasmidDTO.getSubProjectId());
        if (cerSubProjectTb == null) {
            throw new BusinessException("未找到子项目信息 subProjectId=" + plasmidDTO.getSubProjectId());
        }
        if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("非执行中项目不能进行该操作");
        }

        //补充form表单
        plasmidDTO.setProjectCode(cerProjectTb.getProjectCode());
        plasmidDTO.setProjectName(cerProjectTb.getProjectName());
        plasmidDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        plasmidDTO.setSubProjectName(cerSubProjectTb.getSubProjectName());
        plasmidDTO.setGeneEditMethod(cerProjectTb.getGeneEditMethod());
        plasmidDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
        plasmidDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plasmidDTO));
        log.info("【任务工单】质粒质检校验结束");


        /**
         * 更新当前执行步骤
         */
        logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.plasmid_check, bioTaskDtlTb.getTaskNum());
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {

            PlasmidDTO plasmidDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);

            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(plasmidDTO.getVectorTaskId());

            //更新转化质检结果
            for (PlasmidDTO.Content content : plasmidDTO.getContentList()) {
                CerVectorGroupTb cerVectorGroupTb = cerVectorGroupTbMapper.selectOneByGroupNameAndVectorTaskId(content.getPlasmidName(), cerVectorTaskTb.getId());
                if (cerVectorGroupTb == null) {
                    throw new BusinessException("载体任务：" + cerVectorTaskTb.getVectorTaskCode() + "中无此转化:" + content.getPlasmidName());
                }
                CerPlasmidQualityTb cerPlasmidQualityTb = new CerPlasmidQualityTb();
                cerPlasmidQualityTb.setSubProjectId(plasmidDTO.getSubProjectId());
                cerPlasmidQualityTb.setProjectId(plasmidDTO.getProjectId());
                cerPlasmidQualityTb.setVectorTaskId(plasmidDTO.getVectorTaskId());
                cerPlasmidQualityTb.setPlasmidName(content.getPlasmidName());
                cerPlasmidQualityTb.setQualityInspectionNumber(content.getPlasmidName());
                cerPlasmidQualityTb.setQualityInspectionResult(content.getQualityInspectionResult());
                cerPlasmidQualityTb.setAgrobacteriumInformation(content.getAgrobacteriumInformation());
                cerPlasmidQualityTb.setCreateUserName(SecurityContextHolder.getNickName());
                cerPlasmidQualityTb.setCreateUserId(SecurityContextHolder.getUserId());
                cerPlasmidQualityTb.setUpdateTime(new Date());
                cerPlasmidQualityTb.setCreateTime(new Date());
                cerPlasmidQualityTb.setQualityInspectionType(content.getQualityInspectionType());
                cerPlasmidQualityTb.setAgrobacteriumResistance(content.getAgrobacteriumResistance());
                cerPlasmidQualityTb.setPlasmidConcentration(content.getPlasmidConcentration());
                cerPlasmidQualityTb.setExtractionKit(content.getExtractionKit());
                cerPlasmidQualityTb.setTaskStatus(bioTaskDtlTb.getTaskStatus());
                cerPlasmidQualityTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                cerPlasmidQualityTb.setFileUrls(JSONUtil.toJsonStr(content.getFileUrlList()));
                cerPlasmidQualityTb.setProjectCode(cerVectorTaskTb.getProjectCode());
                cerPlasmidQualityTb.setSubProjectCode(cerVectorTaskTb.getSubProjectCode());
                cerPlasmidQualityTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                cerPlasmidQualityTb.setRemark(content.getRemark());
                cerPlasmidQualityTbMapper.insert(cerPlasmidQualityTb);
                content.setPlasmidName(content.getPlasmidName());
            }

            //质检中
            cerVectorTaskTb.setQualityInspectionResult(QualityInspectionResultEnum.checking.name());
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

            updateVectorTaskQualityInspectionResult(bioTaskDtlTb);

        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        cerPlasmidQualityTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
        PlasmidDTO plasmidDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
        cerVectorTaskTbMapper.updateQualityInspectionResultById(QualityInspectionResultEnum.nocheck.name(), plasmidDTO.getVectorTaskId());
        cerVectorStepLogMapper.deleteByTaskNumAndStepCode(bioTaskDtlTb.getTaskNum(),ImplementationPlanTypeEnum.plasmid_check.name());
    }

    /**
     * @param bioTaskDtlTb
     */
    private void updateVectorTaskQualityInspectionResult(BioTaskDtlTb bioTaskDtlTb) {
        //更新审核结果到载体中(以第一次审核结果为准)
        List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectAllByTaskNum(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isEmpty(cerPlasmidQualityTbList)) {
            return;
        }
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(cerPlasmidQualityTbList.get(0).getVectorTaskId());

        cerPlasmidQualityTbList = cerPlasmidQualityTbList.stream().filter(cerPlasmidQualityTb -> QualityInspectionResultEnum.refuse.name().equals(cerPlasmidQualityTb.getQualityInspectionResult())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(cerPlasmidQualityTbList)) {
            //质检未通过
            cerVectorTaskTb.setQualityInspectionResult(QualityInspectionResultEnum.refuse.name());
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
        } else {
            cerVectorTaskTb.setQualityInspectionResult(QualityInspectionResultEnum.pass.name());
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
        }

        updateVectorTaskTimePlan(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.plasmid_check);
    }
}
