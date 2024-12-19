package com.bio.drqi.manage.service.task.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.base.SampleUnitDTO;
import com.bio.drqi.enums.*;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.NewSampleTestDTO;
import com.bio.drqi.manage.service.project.SampleTestService;
import com.bio.drqi.manage.util.SampleCodeUtil;
import com.bio.drqi.mapper.*;
import com.bio.drqi.sample.req.LayoutConfirmReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("sample_and_test")
public class NewSampleTestProcService extends AbstractBaseProjectTaskService {

    private static final String oneTestType = "one";
    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private CerSampleApplyTbMapper cerSampleApplyTbMapper;
    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerSampleCodePrefixTbMapper cerSampleCodePrefixTbMapper;

    @Resource
    private SampleTestService sampleTestService;

    @Override
    public void taskCheck(BioTaskDtlTb bioTaskDtlTb) {
        NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
        if (newSampleTestDTO == null) {
            throw new BusinessException("工单无表单信息");
        }
        //重复取样数据校验
        if (CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList())) {
            for (NewSampleTestDTO.RepeatSampleApply repeatSampleApply : newSampleTestDTO.getRepeatSampleApplyList()) {
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(repeatSampleApply.getVectorTaskCode());
                CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
                if (cerProjectTb == null) {
                    throw new BusinessException("未找到项目信息 projectCode=" + cerVectorTaskTb.getProjectCode());
                }
                if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
                    throw new BusinessException(cerVectorTaskTb.getProjectCode() + "项目不是进行中");
                }
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByVectorTaskCodeAndSampleCode(cerVectorTaskTb.getVectorTaskCode(), repeatSampleApply.getSampleCode());
                if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                    throw new BusinessException(cerVectorTaskTb.getVectorTaskCode() + "实施方案中取样编号找不到：" + repeatSampleApply.getSampleCode());
                }

            }
        }
        //如果首次取样，进行转化信息校验
        if (CollectionUtil.isNotEmpty(newSampleTestDTO.getFirstSampleApplyList())) {
            for (NewSampleTestDTO.FirstSampleApply firstSampleApply : newSampleTestDTO.getFirstSampleApplyList()) {

                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(firstSampleApply.getTransformCode(), firstSampleApply.getVectorTaskCode());
                if (cerTransformTb == null) {
                    throw new BusinessException("此实施方案下查询不到转化信息：" + firstSampleApply.getTransformCode());
                }
                if (!BioTaskStatusEnum.TASK_STATUS_2.status.equals(cerTransformTb.getTaskStatus())) {
                    throw new BusinessException("该转化未审批通过：" + cerTransformTb.getTransformCode());
                }
                CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerTransformTb.getProjectCode());
                if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
                    throw new BusinessException(cerTransformTb.getTransformCode() + "所属项目不是进行中");
                }

            }
        }
        //数据入库
        synchronized (this) {
            CerSampleApplyTb cerSampleApplyTb = cerSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());
            if (cerSampleApplyTb == null) {
                synchronized (this) {
                    doInitProjectData(bioTaskDtlTb);
                    //如果是单管，则直接默认生成模板
                    if (oneTestType.equals(newSampleTestDTO.getTestType())) {
                        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
                        LayoutConfirmReqDTO layoutConfirmReqDTO = new LayoutConfirmReqDTO();
                        cerSampleTestTbList.forEach(cerSampleTestTb -> layoutConfirmReqDTO.fillSampleToNinetySixList(cerSampleTestTb.getVectorTaskCode(),cerSampleTestTb.getTransformCode(),cerSampleTestTb.getSampleCode(),cerSampleTestTb.getIdentifyPrimer()));
                        sampleTestService.layoutConfirm(layoutConfirmReqDTO);
                    }

                }
            }
        }
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        CerSampleApplyTb cerSampleApplyTb = cerSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());
        cerSampleTestTbMapper.updateCheckResultByApplyNoAndCheckResultIsNull("舍弃", cerSampleApplyTb.getApplyNo());
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        cerSampleApplyTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        cerSampleTestTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
    }


    private void doInitProjectData(BioTaskDtlTb bioTaskDtlTb) {
        NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
        Integer applyNumber = CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList()) ? newSampleTestDTO.getRepeatSampleApplyList().size() : null;
        if (applyNumber == null) {
            applyNumber = CollectionUtil.isNotEmpty(newSampleTestDTO.getFirstSampleApplyList()) ? newSampleTestDTO.getFirstSampleApplyList().stream().mapToInt(NewSampleTestDTO.FirstSampleApply::getSampleNum).sum() : null;
        }
        //取样检测申请记录
        CerSampleApplyTb cerSampleApplyTb = new CerSampleApplyTb();
        cerSampleApplyTb.setApplyNo(bioTaskDtlTb.getTaskNum());
        cerSampleApplyTb.setApplyNumber(applyNumber);
        cerSampleApplyTb.setApplyTime(new Date());
        cerSampleApplyTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        cerSampleApplyTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        cerSampleApplyTb.setCurrentStepCode(SampleTaskStatusEnum.STATUS_0.status);
        cerSampleApplyTb.setApplyDesc(bioTaskDtlTb.getTaskDesc());
        cerSampleApplyTb.setApplyType(CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList()) ? SampleApplyTypeEnum.R.name() : SampleApplyTypeEnum.F.name());
        cerSampleApplyTbMapper.insert(cerSampleApplyTb);

        //重复取样申请
        if (CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList())) {
            List<CerSampleTestTb> targetCerSampleTestTbList = new ArrayList<>();
            for (NewSampleTestDTO.RepeatSampleApply repeatSampleApply : newSampleTestDTO.getRepeatSampleApplyList()) {
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(repeatSampleApply.getVectorTaskCode());
                CerSampleTestTb orgCerSampleTestTb = cerSampleTestTbMapper.selectOneByUniqueCode(cerVectorTaskTb.getProjectCode() + repeatSampleApply.getSampleCode());
                if (orgCerSampleTestTb == null) {
                    throw new BusinessException("实施方案中:" + cerVectorTaskTb.getVectorTaskCode() + "中无此取样编号" + repeatSampleApply.getSampleCode());
                }
                CerSampleTestTb repeatCerSampleTestTb = new CerSampleTestTb();
                repeatCerSampleTestTb.setProjectId(orgCerSampleTestTb.getProjectId());
                repeatCerSampleTestTb.setSubProjectId(orgCerSampleTestTb.getSubProjectId());
                repeatCerSampleTestTb.setVectorTaskId(orgCerSampleTestTb.getVectorTaskId());
                repeatCerSampleTestTb.setProjectCode(orgCerSampleTestTb.getProjectCode());
                repeatCerSampleTestTb.setSubProjectCode(orgCerSampleTestTb.getSubProjectCode());
                repeatCerSampleTestTb.setVectorTaskCode(orgCerSampleTestTb.getVectorTaskCode());
                repeatCerSampleTestTb.setPlasmidName(orgCerSampleTestTb.getPlasmidName());
                repeatCerSampleTestTb.setTransformCode(orgCerSampleTestTb.getTransformCode());
                repeatCerSampleTestTb.setSampleCode(orgCerSampleTestTb.getSampleCode());
                repeatCerSampleTestTb.setApplyTime(new Date());
                repeatCerSampleTestTb.setApplyUserId(SecurityContextHolder.getUserId());
                repeatCerSampleTestTb.setApplyUserName(SecurityContextHolder.getNickName());
                repeatCerSampleTestTb.setAcceptorMaterial(orgCerSampleTestTb.getAcceptorMaterial());
                repeatCerSampleTestTb.setCreateTime(new Date());
                repeatCerSampleTestTb.setApplyNo(cerSampleApplyTb.getApplyNo());
                targetCerSampleTestTbList.add(repeatCerSampleTestTb);
            }
            try {
                cerSampleTestTbMapper.insertBatch(targetCerSampleTestTbList);
            } catch (DuplicateKeyException e) {
                log.error("取样申请异常", e);
                throw new BusinessException("取样编号有重复");
            }

            newSampleTestDTO.getRepeatSampleApplyList().stream().map(NewSampleTestDTO.RepeatSampleApply::getVectorTaskCode).distinct().forEach(vectorTaskCode -> {
                /**
                 * 更新当前执行步骤
                 */
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
                logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.sample_and_test, bioTaskDtlTb.getTaskNum());
            });
        }
        //首次取样申请
        if (CollectionUtil.isNotEmpty(newSampleTestDTO.getFirstSampleApplyList())) {
            for (NewSampleTestDTO.FirstSampleApply firstSampleApply : newSampleTestDTO.getFirstSampleApplyList()) {
                List<CerSampleTestTb> targetCerSampleTestTbList = new ArrayList<>();
                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(firstSampleApply.getTransformCode(), firstSampleApply.getVectorTaskCode());
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerTransformTb.getVectorTaskCode());
                CerSampleCodePrefixTb cerSampleCodePrefixTb = cerSampleCodePrefixTbMapper.selectOneByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                for (int i = 1; i <= firstSampleApply.getSampleNum(); i++) {
                    CerSampleTestTb cerSampleTestTb = new CerSampleTestTb();
                    cerSampleTestTb.setProjectId(cerTransformTb.getProjectId());
                    cerSampleTestTb.setSubProjectId(cerTransformTb.getSubProjectId());
                    cerSampleTestTb.setVectorTaskId(cerTransformTb.getVectorTaskId());
                    cerSampleTestTb.setProjectCode(cerTransformTb.getProjectCode());
                    cerSampleTestTb.setSubProjectCode(cerTransformTb.getSubProjectCode());
                    cerSampleTestTb.setVectorTaskCode(cerTransformTb.getVectorTaskCode());
                    cerSampleTestTb.setPlasmidName(cerTransformTb.getPlasmidName());
                    cerSampleTestTb.setTransformCode(cerTransformTb.getTransformCode());
                    cerSampleTestTb.setSampleCode(cerSampleCodePrefixTb.getSampleCodePrefix() + (cerSampleCodePrefixTb.getCurrentIndex() + i - 1));
                    cerSampleTestTb.setApplyTime(new Date());
                    cerSampleTestTb.setApplyUserId(SecurityContextHolder.getUserId());
                    cerSampleTestTb.setApplyUserName(SecurityContextHolder.getNickName());
                    cerSampleTestTb.setAcceptorMaterial(cerTransformTb.getAcceptorMaterial());
                    cerSampleTestTb.setCreateTime(new Date());
                    cerSampleTestTb.setApplyNo(cerSampleApplyTb.getApplyNo());
                    cerSampleTestTb.setUniqueCode(cerTransformTb.getProjectCode() + cerSampleTestTb.getSampleCode());
                    targetCerSampleTestTbList.add(cerSampleTestTb);
                }
                cerSampleCodePrefixTb.setCurrentIndex(cerSampleCodePrefixTb.getCurrentIndex() + firstSampleApply.getSampleNum());
                cerSampleCodePrefixTbMapper.updateById(cerSampleCodePrefixTb);

                /**
                 * 更新当前执行步骤
                 */
                logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.sample_and_test, bioTaskDtlTb.getTaskNum());

                updateVectorTaskTimePlan(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.sample_and_test);

                try {
                    cerSampleTestTbMapper.insertBatch(targetCerSampleTestTbList);
                } catch (DuplicateKeyException e) {
                    log.error("取样申请异常", e);
                    throw new BusinessException("取样编号有重复");
                }
            }
        }
    }


}

