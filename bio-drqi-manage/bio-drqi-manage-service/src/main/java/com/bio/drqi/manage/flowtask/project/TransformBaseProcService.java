package com.bio.drqi.manage.flowtask.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.*;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.TransformDTO;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("transform")
@Slf4j
public class TransformBaseProcService extends AbstractProjectBaseTaskService {


    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;


    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        log.info("【任务工单】转化再生开始");
        TransformDTO transformDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TransformDTO.class);
        if (transformDTO == null) {
            throw new BusinessException("工单关联表单信息缺失 工单号：" + bioTaskDtlTb.getTaskNum());
        }
        ValidatorUtil.validator(transformDTO);

        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(transformDTO.getProjectId());
        if (cerProjectTb == null) {
            throw new BusinessException("未找到项目信息 projectId=" + transformDTO.getProjectId());
        }
        if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("非执行中项目不能进行该操作");
        }
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectById(transformDTO.getSubProjectId());
        if (cerSubProjectTb == null) {
            throw new BusinessException("未找到子项目信息 subProjectId=" + transformDTO.getSubProjectId());
        }
        if (!cerProjectTb.getId().equals(cerSubProjectTb.getProjectId())) {
            throw new BusinessException("参数异常：子项目所属项目不匹配");
        }
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(transformDTO.getVectorTaskId());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("未匹配到载体任务 vectorTaskId=" + transformDTO.getVectorTaskId());
        }
        if (!cerSubProjectTb.getId().equals(cerVectorTaskTb.getSubProjectId())) {
            throw new BusinessException("参数异常：载体任务未匹配子项目");
        }
        if (!BioTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
            throw new BusinessException("不是进行中实施方案,实施方案号：" + cerVectorTaskTb.getVectorTaskCode());
        }
        CerVectorStepLog cerVectorStepLog = cerVectorStepLogMapper.selectOneByVectorTaskIdAndStepCode(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.plasmid_check.name());
        if (cerVectorStepLog == null) {
            throw new BusinessException("只有质检通过任务方可进行转化");
        }
        //补充form表单
        transformDTO.setProjectCode(cerProjectTb.getProjectCode());
        transformDTO.setProjectName(cerProjectTb.getProjectName());
        transformDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        transformDTO.setGeneEditMethod(cerProjectTb.getGeneEditMethod());
        transformDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(transformDTO));
        log.info("【任务工单】转化再生完毕完毕");

        //填充个性化处理，实施方案条件判断值
        List<String> list= new ArrayList<>();
        list.add(transformDTO.getVectorTaskCode());
        transformDTO.setConditionVectorTaskCodeList(list);

        /**
         * 更新当前执行步骤
         */
        logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.transform, bioTaskDtlTb.getTaskNum());
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            log.info("【任务工单】转化再生开始");
            TransformDTO transformDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TransformDTO.class);
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(transformDTO.getVectorTaskId());
            if (!BioTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
                throw new BusinessException("不是进行中实施方案,实施方案号：" + cerVectorTaskTb.getVectorTaskCode());
            }
            for (TransformDTO.Content content : transformDTO.getContentList()) {
                CerTransformTb cerTransformTb = new CerTransformTb();
                cerTransformTb.setProjectId(cerVectorTaskTb.getProjectId());
                cerTransformTb.setProjectCode(cerVectorTaskTb.getProjectCode());
                cerTransformTb.setVectorTaskId(cerVectorTaskTb.getId());
                cerTransformTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                cerTransformTb.setSubProjectId(cerVectorTaskTb.getSubProjectId());
                cerTransformTb.setSubProjectCode(cerVectorTaskTb.getSubProjectCode());
                cerTransformTb.setInfectNumber(content.getInfectNumber());
                cerTransformTb.setInfectDate(content.getInfectDate());
                cerTransformTb.setDeliveryMethod(content.getDeliveryMethod());
                cerTransformTb.setTransformCode(getTransFormCode(cerVectorTaskTb.getVectorTaskCode(), content.getDeliveryMethod(), content.getInfectDate()));
                cerTransformTb.setAcceptorMaterial(content.getAcceptorMaterial());
                if (cerVectorTaskTb.getBreedCode().contains("|")) {
                    CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(content.getAcceptorMaterial(), cerVectorTaskTb.getSpeciesCode());
                    if (cerBreedDict != null) {
                        cerTransformTb.setBreedCode(cerBreedDict.getBreedCode());
                    } else {
                        cerTransformTb.setBreedCode(cerVectorTaskTb.getBreedCode().split("\\|")[0]);
                    }
                } else {
                    cerTransformTb.setBreedCode(cerVectorTaskTb.getBreedCode());
                }
                cerTransformTb.setCreateTime(new Date());
                cerTransformTb.setUpdateTime(new Date());
                cerTransformTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
                cerTransformTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
                cerTransformTb.setTaskStatus(bioTaskDtlTb.getTaskStatus());
                cerTransformTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                cerTransformTb.setSpeciesCode(cerVectorTaskTb.getSpeciesCode());
                try {
                    cerTransformTbMapper.insert(cerTransformTb);
                } catch (DuplicateKeyException e) {
                    throw new BusinessException("转化编号已经存在：" + cerTransformTb.getTransformCode());
                }

                content.setTransformCode(cerTransformTb.getTransformCode());
            }

            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(transformDTO));
            log.info("【任务工单】转化再生完毕完毕");
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        cerVectorStepLogMapper.deleteByTaskNumAndStepCode(bioTaskDtlTb.getTaskNum(), ImplementationPlanTypeEnum.transform.name());
    }


    private String getTransFormCode(String vectorTaskCode, String deliveryMethod, String infectDate) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(cerVectorTaskTb.getSpeciesCode());
        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllByTransformCodeLike(cerSpeciesConf.getNumPrefix().substring(2) + deliveryMethod + infectDate.replace("-", "").substring(4));
        cerTransformTbList = cerTransformTbList.stream().filter(cerTransformTb -> cerTransformTb.getTransformCode().matches("^[A-Z]{3}[0-9]{6}$")).collect(Collectors.toList());
        String nextNumber = null;
        if (CollectionUtil.isEmpty(cerTransformTbList)) {
            nextNumber = "01";
        } else {
            Integer maxInt = cerTransformTbList.stream().map(cerTransformTb -> Integer.valueOf(cerTransformTb.getTransformCode().substring(7))).max(Integer::compare).get();
            nextNumber = StringUtils.padl(String.valueOf(maxInt + 1), 2, '0');
        }
        return cerSpeciesConf.getNumPrefix().substring(2) + deliveryMethod + infectDate.replace("-", "").substring(4) + nextNumber;
    }


}
