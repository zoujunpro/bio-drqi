package com.bio.drqi.manage.flowtask.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.manage.dto.project.PlasmidDTO;
import com.bio.drqi.mapper.CerPlasmidQualityTbMapper;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.bio.drqi.mapper.CerSubProjectTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        log.info("【任务工单】质粒质检校验开始");
        PlasmidDTO plasmidDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
        ValidatorUtil.validator(plasmidDTO);
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(plasmidDTO.getVectorTaskId());
        if (!BioTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
            throw new BusinessException("不是进行中实施方案,实施方案号：" + cerVectorTaskTb.getVectorTaskCode());
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
        List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        if (CollectionUtil.isNotEmpty(cerPlasmidQualityTbList)) {
            throw new BusinessException("已经做过质粒质检");
        }
        //补充form表单
        plasmidDTO.setProjectCode(cerProjectTb.getProjectCode());
        plasmidDTO.setProjectName(cerProjectTb.getProjectName());
        plasmidDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        plasmidDTO.setGeneEditMethod(cerProjectTb.getGeneEditMethod());
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
            if (!BioTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
                throw new BusinessException("不是进行中实施方案,实施方案号：" + cerVectorTaskTb.getVectorTaskCode());
            }
            //更新转化质检结果
            for (PlasmidDTO.Content content : plasmidDTO.getContentList()) {
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
            }
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        cerPlasmidQualityTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
        cerVectorStepLogMapper.deleteByTaskNumAndStepCode(bioTaskDtlTb.getTaskNum(), ImplementationPlanTypeEnum.plasmid_check.name());
    }


    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        PlasmidDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();

        List<BioHtmlModelDTO.ModelField> fieldList = new ArrayList<>();
        fieldList.add(buildField("项目名称", dto.getProjectName()));
        fieldList.add(buildField("项目编号", dto.getProjectCode()));
        fieldList.add(buildField("子项目编号", dto.getSubProjectCode()));
        fieldList.add(buildField("实施方案编号", dto.getVectorTaskCode()));
        sections.add(buildFieldSection("申请信息", fieldList));

        if (CollectionUtil.isNotEmpty(dto.getContentList())) {
            List<String> headers = Arrays.asList("质粒名称", "下一步安排", "质检结果", "农杆菌信息", "农杆菌抗性", "质粒浓度", "提取试剂盒", "备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (PlasmidDTO.Content item : dto.getContentList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("质粒名称", item.getPlasmidName());
                row.put("下一步安排", qualityInspectionTypeName(item.getQualityInspectionType()));
                row.put("质检结果", qualityInspectionResultName(item.getQualityInspectionResult()));
                row.put("农杆菌信息", item.getAgrobacteriumInformation());
                row.put("农杆菌抗性", item.getAgrobacteriumResistance());
                row.put("质粒浓度", item.getPlasmidConcentration());
                row.put("提取试剂盒", item.getExtractionKit());
                row.put("备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("质粒质检明细", headers, rows));
        }

        return sections;
    }

    private String qualityInspectionTypeName(String code) {
        if (code == null) {
            return null;
        }
        List<String> codeList;
        if (code.startsWith("[")) {
            codeList = JSONUtil.toList(code, String.class);
        } else {
            codeList = Collections.singletonList(code);
        }
        List<String> nameList = new ArrayList<>();
        for (String item : codeList) {
            if ("1".equals(item)) {
                nameList.add("质粒制备");
            } else if ("2".equals(item)) {
                nameList.add("农杆菌转化");
            } else if (item != null) {
                nameList.add(item);
            }
        }
        return String.join("、", nameList);
    }

    private String qualityInspectionResultName(String code) {
        if ("pass".equals(code)) {
            return "合格";
        }
        if ("refuse".equals(code)) {
            return "不合格";
        }
        return code;
    }
}
