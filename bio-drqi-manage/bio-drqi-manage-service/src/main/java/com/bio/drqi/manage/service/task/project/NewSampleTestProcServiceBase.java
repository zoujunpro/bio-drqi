package com.bio.drqi.manage.service.task.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.*;
import com.bio.drqi.manage.dto.project.NewSampleTestDTO;
import com.bio.drqi.manage.sample.req.LayoutConfirmReqDTO;
import com.bio.drqi.manage.service.project.SampleTestService;
import com.bio.drqi.mapper.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("sample_and_test")
public class NewSampleTestProcServiceBase extends AbstractProjectBaseTaskService {

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

    @Resource
    private CerSampleLayoutTbMapper cerSampleLayoutTbMapper;

    @Resource
    private BioSampleSampleTwoResultDetailTbMapper bioSampleSampleTwoResultDetailTbMapper;

    @Resource
    private BioSampleSampleTwoResultTbMapper bioSampleSampleTwoResultTbMapper;

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private BioSampleSampleOneResultTbMapper bioSampleSampleOneResultTbMapper;


    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
        if (newSampleTestDTO == null) {
            throw new BusinessException("工单无表单信息");
        }
        ValidatorUtil.validator(newSampleTestDTO);

        //重复取样数据校验
        if (CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList())) {
            for (NewSampleTestDTO.RepeatSampleApply repeatSampleApply : newSampleTestDTO.getRepeatSampleApplyList()) {
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(repeatSampleApply.getVectorTaskCode());
                CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
                if (cerProjectTb == null) {
                    throw new BusinessException("未找到项目信息 projectCode=" + cerVectorTaskTb.getProjectCode());
                }
                if (!StringUtils.equals(cerVectorTaskTb.getSpeciesCode(), newSampleTestDTO.getSpeciesCode())) {
                    throw new BusinessException("取样物种不是所规定物种,取样编号：" + repeatSampleApply.getSampleCode());
                }
                if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
                    throw new BusinessException(cerVectorTaskTb.getProjectCode() + "项目不是进行中");
                }
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByVectorTaskCodeAndSampleCode(cerVectorTaskTb.getVectorTaskCode(), repeatSampleApply.getSampleCode());
                if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                    throw new BusinessException(cerVectorTaskTb.getVectorTaskCode() + "实施方案中取样编号找不到：" + repeatSampleApply.getSampleCode());
                }
            }
            if (newSampleTestDTO.isCloneFlag()) {
                List list = newSampleTestDTO.getRepeatSampleApplyList().stream().filter(repeatSampleApply -> repeatSampleApply.getCloneNum() == null || repeatSampleApply.getCloneNum() < 1).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(list)) {
                    throw new BusinessException("克隆苗数量必填");
                }
                if (newSampleTestDTO.getRepeatSampleApplyList().stream().filter(repeatSampleApply -> repeatSampleApply.getSampleCode().contains("-")).collect(Collectors.toList()).size() > 0) {
                    throw new BusinessException("不能基于克隆苗进行二次克隆苗取样");
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
                        layoutConfirmReqDTO.setApplyNo(bioTaskDtlTb.getTaskNum());
                        for (CerSampleTestTb cerSampleTestTb : cerSampleTestTbList) {
                            layoutConfirmReqDTO.fillSampleToSingleList(cerSampleTestTb.getVectorTaskCode(), cerSampleTestTb.getTransformCode(), cerSampleTestTb.getSampleCode(), cerSampleTestTb.getIdentifyPrimer());
                        }
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
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            cerSampleTestTbMapper.updateCheckResultByApplyNoAndCheckResultIsNull("舍弃", cerSampleApplyTb.getApplyNo());
            //首次取样，且已经发生过移苗
            if (SampleApplyTypeEnum.F.name().equals(cerSampleApplyTb.getApplyType())) {
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(cerSampleApplyTb.getApplyNo()).stream().filter(cerSampleTestTb -> "传代".equals(cerSampleTestTb.getCheckResult()) || "留种".equals(cerSampleTestTb.getCheckResult())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(cerSampleTestTbList)) {
                    for (CerSampleTestTb cerSampleTestTb : cerSampleTestTbList) {
                        List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectAllByTransformCodeAndVectorTaskCode(cerSampleTestTb.getTransformCode(), cerSampleTestTb.getVectorTaskCode());
                        if (CollectionUtil.isNotEmpty(cerConversionAndTransRefList)) {
                            CerPlantDtlTb cerPlantDtlTb = CerPlantDtlTb.of(cerSampleTestTb, SecurityContextHolder.getUserId(), SecurityContextHolder.getNickName(), bioTaskDtlTb.getTaskNum());
                            cerPlantDtlTb.setPlantCode(cerSampleTestTb.getSampleCode());
                            cerPlantDtlTb.setPlantStatus(PlantStatusEnum.STATUS_1.code);
                            cerPlantDtlTb.setTransplantDate(DateUtil.format(cerConversionAndTransRefList.get(0).getCreateTime(), DatePattern.NORM_DATE_PATTERN));
                            if (Objects.isNull(cerPlantDtlTbMapper.selectOneByPlantCode(cerPlantDtlTb.getPlantCode()))) {
                                //更新种植信息
                                cerPlantDtlTbMapper.insert(cerPlantDtlTb);
                                /**
                                 * 更新当前执行步骤
                                 */
                                logStep(cerSampleTestTb.getVectorTaskId(), ImplementationPlanTypeEnum.cer_plant, bioTaskDtlTb.getTaskNum());
                            }
                        }

                    }

                }
            }


        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(cerSampleTestTbList)) {
            GsonBuilder builder = new GsonBuilder();
            builder.serializeNulls(); // 启用序列化null值
            Gson gson = builder.create();
            NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
            newSampleTestDTO.setCancelTaskSampleList(gson.toJson(cerSampleTestTbList));
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
        }
        cerSampleApplyTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        cerSampleTestTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        cerSampleLayoutTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        List<BioSampleSampleTwoResultTb> bioSampleSampleTwoResultTbList = bioSampleSampleTwoResultTbMapper.selectAllByUploadNum(bioTaskDtlTb.getTaskNum());
        if(CollectionUtil.isNotEmpty(bioSampleSampleTwoResultTbList)){
            bioSampleSampleTwoResultTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
            bioSampleSampleTwoResultTbList.forEach(bioSampleSampleTwoResultTb->{
                bioSampleSampleTwoResultDetailTbMapper.deleteByApplyNoAndSampleCode(bioSampleSampleTwoResultTb.getApplyNo(),bioSampleSampleTwoResultTb.getSampleCode());
            });
        }

        cerPlantDtlTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
        bioSampleSampleOneResultTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
        cerVectorStepLogMapper.deleteByTaskNumAndStepCode(bioTaskDtlTb.getTaskNum(), ImplementationPlanTypeEnum.cer_plant.name());
        cerVectorStepLogMapper.deleteByTaskNumAndStepCode(bioTaskDtlTb.getTaskNum(), ImplementationPlanTypeEnum.sample_and_test.name());

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
        cerSampleApplyTb.setApplyDesc(bioTaskDtlTb.getTaskDesc());
        cerSampleApplyTb.setApplyType(CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList()) ? SampleApplyTypeEnum.R.name() : SampleApplyTypeEnum.F.name());
        cerSampleApplyTb.setIdentifyExcelUrl(null);
        cerSampleApplyTb.setOneTestExcelUrl(null);
        cerSampleApplyTb.setNgsExcelUrl(null);
        cerSampleApplyTb.setCloneFlag(newSampleTestDTO.isCloneFlag() ? BioDrQiContents.Y : BioDrQiContents.N);
        cerSampleApplyTb.setLayoutFlag(newSampleTestDTO.getTestType());
        cerSampleApplyTb.setVectorTaskCodes(null);
        cerSampleApplyTb.setSampleCodeRange(null);
        cerSampleApplyTbMapper.insert(cerSampleApplyTb);

        //重复取样申请
        if (CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList())) {
            List<CerSampleTestTb> targetCerSampleTestTbList = new ArrayList<>();
            //克隆苗
            if (newSampleTestDTO.isCloneFlag()) {
                for (NewSampleTestDTO.RepeatSampleApply repeatSampleApply : newSampleTestDTO.getRepeatSampleApplyList()) {
                    List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllBySampleCodeLike(repeatSampleApply.getSampleCode());
                    List<Integer> sampleCodeSuffixList = cerSampleTestTbList.stream().filter(cerSampleTestTb -> cerSampleTestTb.getSampleCode().contains("-")).map(cerSampleTestTb -> Integer.valueOf(cerSampleTestTb.getSampleCode().substring(cerSampleTestTb.getSampleCode().indexOf("-") + 1))).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                    int maxSampleCodeSuffix = CollectionUtil.isNotEmpty(sampleCodeSuffixList) ? Integer.valueOf(sampleCodeSuffixList.get(0)) : 0;
                    for (int i = 1; i <= repeatSampleApply.getCloneNum(); i++) {
                        CerSampleTestTb repeatCerSampleTestTb = new CerSampleTestTb();
                        repeatCerSampleTestTb.setProjectId(cerSampleTestTbList.get(0).getProjectId());
                        repeatCerSampleTestTb.setSubProjectId(cerSampleTestTbList.get(0).getSubProjectId());
                        repeatCerSampleTestTb.setVectorTaskId(cerSampleTestTbList.get(0).getVectorTaskId());
                        repeatCerSampleTestTb.setProjectCode(cerSampleTestTbList.get(0).getProjectCode());
                        repeatCerSampleTestTb.setSubProjectCode(cerSampleTestTbList.get(0).getSubProjectCode());
                        repeatCerSampleTestTb.setVectorTaskCode(cerSampleTestTbList.get(0).getVectorTaskCode());
                        repeatCerSampleTestTb.setTransformCode(cerSampleTestTbList.get(0).getTransformCode());
                        repeatCerSampleTestTb.setSampleCode(repeatSampleApply.getSampleCode() + "-" + (maxSampleCodeSuffix + i));
                        repeatCerSampleTestTb.setApplyTime(new Date());
                        repeatCerSampleTestTb.setApplyUserId(SecurityContextHolder.getUserId());
                        repeatCerSampleTestTb.setApplyUserName(SecurityContextHolder.getNickName());
                        repeatCerSampleTestTb.setAcceptorMaterial(cerSampleTestTbList.get(0).getAcceptorMaterial());
                        repeatCerSampleTestTb.setCreateTime(new Date());
                        repeatCerSampleTestTb.setApplyNo(cerSampleApplyTb.getApplyNo());
                        repeatCerSampleTestTb.setSampleTime(repeatSampleApply.getSampleTime());
                        repeatCerSampleTestTb.setSampleGeneration(cerSampleTestTbList.get(0).getSampleGeneration());
                        repeatCerSampleTestTb.setCloneSampleCode(repeatSampleApply.getSampleCode());
                        repeatCerSampleTestTb.setUniqueCode(repeatCerSampleTestTb.getProjectCode() + repeatCerSampleTestTb.getSampleCode());
                        targetCerSampleTestTbList.add(repeatCerSampleTestTb);
                    }
                }

            } else {
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
                    repeatCerSampleTestTb.setCloneSampleCode(orgCerSampleTestTb.getCloneSampleCode());
                    repeatCerSampleTestTb.setApplyTime(new Date());
                    repeatCerSampleTestTb.setApplyUserId(SecurityContextHolder.getUserId());
                    repeatCerSampleTestTb.setApplyUserName(SecurityContextHolder.getNickName());
                    repeatCerSampleTestTb.setAcceptorMaterial(orgCerSampleTestTb.getAcceptorMaterial());
                    repeatCerSampleTestTb.setCreateTime(new Date());
                    repeatCerSampleTestTb.setApplyNo(cerSampleApplyTb.getApplyNo());
                    repeatCerSampleTestTb.setSampleTime(repeatSampleApply.getSampleTime());
                    repeatCerSampleTestTb.setSampleGeneration(orgCerSampleTestTb.getSampleGeneration());
                    targetCerSampleTestTbList.add(repeatCerSampleTestTb);
                }
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
                    cerSampleTestTb.setSampleGeneration(firstSampleApply.getSampleGeneration());
                    cerSampleTestTb.setSampleTime(firstSampleApply.getSampleTime());
                    targetCerSampleTestTbList.add(cerSampleTestTb);
                }

                cerSampleCodePrefixTb.setCurrentIndex(cerSampleCodePrefixTb.getCurrentIndex() + firstSampleApply.getSampleNum());
                cerSampleCodePrefixTbMapper.updateById(cerSampleCodePrefixTb);
                logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.sample_and_test, bioTaskDtlTb.getTaskNum());
                try {
                    cerSampleTestTbMapper.insertBatch(targetCerSampleTestTbList);
                } catch (DuplicateKeyException e) {
                    log.error("取样申请异常", e);
                    throw new BusinessException("取样编号有重复");
                }
            }

            //如果是首次申请，更新申请中包含的事实方案和取样编号范围
            List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
            Map<String, List<CerSampleTestTb>> cerSampleTestTbListMap = cerSampleTestTbList.stream().collect(Collectors.groupingBy(CerSampleTestTb::getVectorTaskCode));
            cerSampleApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(cerSampleTestTbListMap.keySet()).replace("[", "").replace("]", "").replace("\"", ""));
            StringBuffer sampleCodeRangeBuff = new StringBuffer();
            if (SampleApplyTypeEnum.F.name().equals(cerSampleApplyTb.getApplyType())) {
                cerSampleTestTbListMap.forEach((vectorTaskCode, sampleTestList) -> {
                    CerSampleCodePrefixTb cerSampleCodePrefixTb = cerSampleCodePrefixTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
                    sampleTestList = sampleTestList.stream().filter(sampleTest -> sampleTest.getSampleCode().startsWith(cerSampleCodePrefixTb.getSampleCodePrefix())).sorted(Comparator.comparing(sampleTest -> Integer.valueOf(sampleTest.getSampleCode().substring(2)))).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(sampleTestList)) {
                        sampleCodeRangeBuff.append(sampleTestList.get(0).getSampleCode() + "-" + sampleTestList.get(sampleTestList.size() - 1).getSampleCode()).append(",");
                    }
                });
                if (StringUtils.isNotEmpty(sampleCodeRangeBuff.toString())) {
                    cerSampleApplyTb.setSampleCodeRange(sampleCodeRangeBuff.substring(0, sampleCodeRangeBuff.length() - 1));
                }
            }
            cerSampleApplyTbMapper.updateById(cerSampleApplyTb);

        }
    }

}

