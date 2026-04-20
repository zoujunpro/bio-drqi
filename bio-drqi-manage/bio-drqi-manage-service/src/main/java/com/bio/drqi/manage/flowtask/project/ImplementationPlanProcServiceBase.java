package com.bio.drqi.manage.flowtask.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.manage.dto.project.ImplementPlanAddDTO;
import com.bio.drqi.mapper.*;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
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
    private BioSampleCodePrefixTbMapper bioSampleCodePrefixTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

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
                    BioSampleCodePrefixTb bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                    if (bioSampleCodePrefixTb == null) {
                        //生成sampleCodePrefix
                        try {
                            bioSampleCodePrefixTb = new BioSampleCodePrefixTb(createSampleCode(), implementPlanAddDTO.getVectorTaskCode(), bioTaskDtlTb.getTaskNum());
                            bioSampleCodePrefixTbMapper.insert(bioSampleCodePrefixTb);
                        } catch (DuplicateKeyException e) {
                            throw new BusinessException("取样编号前缀重复：" + bioSampleCodePrefixTb.getSampleCodePrefix());
                        }
                    }
                    implementPlanAddDTO.setSampleCodePrefix(bioSampleCodePrefixTb.getSampleCodePrefix());
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
        bioSampleCodePrefixTbMapper.deleteByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
    }

    private String createSampleCode() {
        String sampleCodePrefix = LetterUtil.randomLetter(2);
        List<BioSampleCodePrefixTb> bioSampleCodePrefixTbList = bioSampleCodePrefixTbMapper.selectList(null);
        List<String> sampleCodePrefixList = bioSampleCodePrefixTbList.stream().map(BioSampleCodePrefixTb::getSampleCodePrefix).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(sampleCodePrefixList)) {
            return sampleCodePrefix;
        }
        while (sampleCodePrefixList.contains(sampleCodePrefix)) {
            sampleCodePrefix = LetterUtil.randomLetter(2);
        }
        return sampleCodePrefix;
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        ImplementPlanAddDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ImplementPlanAddDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        CerVectorTaskTb vectorTask = cerVectorTaskTbMapper.selectOneByVectorTaskCode(dto.getVectorTaskCode());
        CerBreedDict breed = StringUtils.isEmpty(dto.getBreedCode()) ? null : cerBreedDictMapper.selectOneByBreedCode(dto.getBreedCode());
        CerSpeciesConf species = StringUtils.isEmpty(dto.getSpeciesCode()) ? null : cerSpeciesConfMapper.selectOneBySpeciesCode(dto.getSpeciesCode());

        List<BioHtmlModelDTO.ModelField> fieldList = new ArrayList<>();
        fieldList.add(buildField("项目名称", dto.getProjectName()));
        fieldList.add(buildField("项目编号", dto.getProjectCode()));
        fieldList.add(buildField("子项目编号", dto.getSubProjectCode()));
        fieldList.add(buildField("实施方案编号", dto.getVectorTaskCode()));
        fieldList.add(buildField("物种", species == null ? dto.getSpeciesCode() : species.getSpeciesName()));
        fieldList.add(buildField("品种", breed == null ? dto.getBreedCode() : breed.getBreedName()));
        fieldList.add(buildField("递送方式", deliveryMethodName(dto.getDeliveryMethod())));
        fieldList.add(buildField("受体材料", dto.getAcceptorMaterial()));
        fieldList.add(buildField("监管级别", supervisionLevelName(dto.getSupervisionLevelCode())));
        fieldList.add(buildField("编辑类型", editTypeName(dto.getEditType())));
        fieldList.add(buildField("期望阳性苗", dto.getExpectedPositiveSeed()));
        fieldList.add(buildField("取样编号前缀", dto.getSampleCodePrefix()));
        fieldList.add(buildField("无质粒递送", yesNoDesc(dto.getNoPlasmidFlag())));
        fieldList.add(buildField("预计开始日期", dto.getExpectStartDate()));
        fieldList.add(buildField("预计项目周期", dto.getExpectPeriod()));

        if (vectorTask != null) {
            fieldList.add(buildField("当前状态", BioTaskStatusEnum.getNameByStatus(vectorTask.getTaskStatus())));
        }

        return Collections.singletonList(buildFieldSection("实施方案信息", fieldList));
    }

    private String deliveryMethodName(String code) {
        if ("A".equals(code)) {
            return "农杆菌转化";
        }
        if ("B".equals(code)) {
            return "基因枪";
        }
        if ("P".equals(code)) {
            return "原生质体转化";
        }
        if ("V".equals(code)) {
            return "病毒载体";
        }
        return code;
    }

    private String supervisionLevelName(String code) {
        if ("1".equals(code)) {
            return "无";
        }
        if ("2".equals(code)) {
            return "DNA-free";
        }
        if ("3".equals(code)) {
            return "transgene-free";
        }
        return code;
    }

    private String editTypeName(String code) {
        if ("1".equals(code)) {
            return "KO";
        }
        if ("2".equals(code)) {
            return "点突变";
        }
        if ("3".equals(code)) {
            return "精准小";
        }
        if ("4".equals(code)) {
            return "精准大";
        }
        if ("5".equals(code)) {
            return "随机转基因";
        }
        return code;
    }

    private String yesNoDesc(String value) {
        if ("Y".equals(value)) {
            return "是";
        }
        if ("N".equals(value)) {
            return "否";
        }
        return value;
    }
}
