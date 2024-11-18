package com.bio.drqi.manage.service.task.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.EditPureUnionEnum;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.ConversionAndTransDTO;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service("conversion_and_trans")
@Slf4j
public class ConversionAndTransProcService extends AbstractBaseProjectTaskService {

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;

    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;


    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    /**
     * 数据校验，暂时不做数据校验
     *
     * @param bioTaskDtlTb
     */
    @Override
    public void taskCheck(BioTaskDtlTb bioTaskDtlTb) {
        ConversionAndTransDTO conversionAndTransDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ConversionAndTransDTO.class);
        if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getTransFormList())) {
            for (ConversionAndTransDTO.TransForm transForm : conversionAndTransDTO.getTransFormList()) {
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(transForm.getVectorTaskCode());
                if (cerVectorTaskTb == null) {
                    throw new BusinessException("实施方案编号不存在：" + transForm.getVectorTaskCode());
                }
                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(transForm.getTransformCode(), cerVectorTaskTb.getVectorTaskCode());
                if (cerTransformTb == null) {
                    throw new BusinessException("此实施方案中无此转化编号：" + transForm.getTransformCode());
                }
                Long number = conversionAndTransDTO.getTransFormList().stream().filter(transForm1 -> StringUtils.equals(transForm1.getVectorTaskCode(), transForm.getVectorTaskCode()) && StringUtils.equals(transForm1.getTransformCode(), transForm.getTransformCode())).count();
                if (number > 1) {
                    throw new BusinessException("提交数据有重复");
                }
            }
        }
        if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getSampleCodeList())) {
            for (ConversionAndTransDTO.SampleCode sample : conversionAndTransDTO.getSampleCodeList()) {
                if (EditPureUnionEnum.valueOf(sample.getEditPureUnion()) == null) {
                    throw new BusinessException("是否编辑纯合不能为空");
                }
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(sample.getVectorTaskCode());
                if (cerVectorTaskTb == null) {
                    throw new BusinessException("实施方案编号不存在：" + sample.getVectorTaskCode());
                }
                CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByVectorTaskCodeAndSampleCodeFirst(cerVectorTaskTb.getVectorTaskCode(), sample.getSampleCode());
                if (cerSampleTestTb == null) {
                    throw new BusinessException("此实施方案中无此取样编号：" + sample.getSampleCode());
                }
                Long number = conversionAndTransDTO.getSampleCodeList().stream().filter(sample1 -> StringUtils.equals(sample1.getVectorTaskCode(), sample.getVectorTaskCode()) && StringUtils.equals(sample1.getSampleCode(), sample.getSampleCode())).count();
                if (number > 1) {
                    throw new BusinessException("提交数据有重复");
                }
            }
        }
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(conversionAndTransDTO));
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            ConversionAndTransDTO conversionAndTransDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ConversionAndTransDTO.class);
            //取样移苗
            if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getSampleCodeList())) {
                for (ConversionAndTransDTO.SampleCode sampleCode : conversionAndTransDTO.getSampleCodeList()) {
                    if (StringUtils.isEmpty(sampleCode.getDealResult())) {
                        sampleCode.setDealResult(CerProjectContents.N);
                    }
                }
            }
            //转化移苗
            if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getTransFormList())) {
                for (ConversionAndTransDTO.TransForm transForm : conversionAndTransDTO.getTransFormList()) {
                    if (StringUtils.isEmpty(transForm.getDealResult())) {
                        transForm.setDealResult(CerProjectContents.N);
                    }
                }
            }
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(conversionAndTransDTO));


            //更新每一个实施方案的添加移苗步骤
            List<String> vectorTaskCodeList = null;
            if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getSampleCodeList())) {
                vectorTaskCodeList = conversionAndTransDTO.getSampleCodeList().stream().map(ConversionAndTransDTO.SampleCode::getVectorTaskCode).distinct().collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getTransFormList())) {
                vectorTaskCodeList = conversionAndTransDTO.getTransFormList().stream().map(ConversionAndTransDTO.TransForm::getVectorTaskCode).distinct().collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(vectorTaskCodeList)) {
                vectorTaskCodeList.forEach(vectorTaskCode -> {
                    CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
                    /**
                     * 更新当前执行步骤
                     */
                    logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.conversion_and_trans, bioTaskDtlTb.getTaskNum());
                });
            }
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        CerConversionAndTransTb cerConversionAndTransTb = cerConversionAndTransTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
        if (cerConversionAndTransTb != null) {
            cerConversionAndTransTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
            cerConversionAndTransRefMapper.deleteByConversionAndTransId(cerConversionAndTransTb.getId());
        }
    }
}
