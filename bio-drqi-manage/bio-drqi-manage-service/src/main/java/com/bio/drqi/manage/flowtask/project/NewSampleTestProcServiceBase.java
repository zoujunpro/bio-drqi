package com.bio.drqi.manage.flowtask.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.*;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.manage.dto.project.NewSampleTestDTO;
import com.bio.drqi.manage.sample.req.LayoutConfirmReqDTO;
import com.bio.drqi.manage.service.bio.BioSampleTestService;
import com.bio.drqi.mapper.*;
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
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private BioSampleTestHisTbMapper bioSampleTestHisTbMapper;

    @Resource
    private BioSampleApplyTbMapper bioSampleApplyTbMapper;
    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private BioSampleCodePrefixTbMapper bioSampleCodePrefixTbMapper;

    @Resource
    private BioSampleTestService bioSampleTestService;

    @Resource
    private BioSampleLayoutTbMapper bioSampleLayoutTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleSampleTwoResultDetailTbMapper;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper;

    @Resource
    private BioSampleTestResultFileTbMapper bioSampleTestResultFileTbMapper;


    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
        if (newSampleTestDTO == null) {
            throw new BusinessException("工单无表单信息");
        }
        ValidatorUtil.validator(newSampleTestDTO);

        //首次取样，进行转化信息校验
        if (CollectionUtil.isNotEmpty(newSampleTestDTO.getFirstSampleApplyList())) {
            for (NewSampleTestDTO.FirstSampleApply firstSampleApply : newSampleTestDTO.getFirstSampleApplyList()) {
                ValidatorUtil.validator(firstSampleApply);
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
        } else {
            for (NewSampleTestDTO.RepeatSampleApply repeatSampleApply : newSampleTestDTO.getRepeatSampleApplyList()) {
                ValidatorUtil.validator(repeatSampleApply);
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(repeatSampleApply.getSampleCode());
                if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
                    throw new BusinessException("系统中找不到此取样信息：" + repeatSampleApply.getSampleCode());
                }

            }
        }
        //取样备注上区分是单管还是孔板取样
        if("one".equals(newSampleTestDTO.getTestType())){
            bioTaskDtlTb.setTaskDesc(bioTaskDtlTb.getTaskDesc()+"(单管取样)");
        }else {
            bioTaskDtlTb.setTaskDesc(bioTaskDtlTb.getTaskDesc()+"(96孔板取样)");
        }
        //数据入库
        synchronized (this) {
            BioSampleApplyTb bioSampleApplyTb = bioSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());
            if (bioSampleApplyTb == null) {
                synchronized (this) {
                    doInitProjectData(bioTaskDtlTb);
                    //如果是单管，则直接默认生成模板
                    if (oneTestType.equals(newSampleTestDTO.getTestType())) {
                        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
                        LayoutConfirmReqDTO layoutConfirmReqDTO = new LayoutConfirmReqDTO();
                        layoutConfirmReqDTO.setApplyNo(bioTaskDtlTb.getTaskNum());
                        for (BioSampleTestTb bioSampleTestTb : bioSampleTestTbList) {
                            layoutConfirmReqDTO.fillSampleToSingleList(bioSampleTestTb.getVectorTaskCode(), bioSampleTestTb.getTransformCode(), bioSampleTestTb.getSampleCode(), bioSampleTestTb.getIdentifyPrimer());
                        }
                        bioSampleTestService.layoutConfirm(layoutConfirmReqDTO);
                    }

                }
            }
        }

        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        BioSampleApplyTb bioSampleApplyTb = bioSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            bioSampleTestTbMapper.updateNoCheckDataByApplyNoAndCheckResult(CheckResultEnum.remove.name(), SecurityContextHolder.getUserId(), SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), SecurityContextHolder.getNickName(), TestResultEnum.noResult.name(), bioSampleApplyTb.getApplyNo(), CheckResultEnum.noCheck.name());
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
            bioSampleTestHisTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
            bioSampleTestHisTbMapper.insertBatch(BeanUtils.copyListProperties(bioSampleTestTbList, BioSampleTestHisTb.class));
        }
        bioSampleApplyTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleTestTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleLayoutTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        List<BioSampleTestTwoResultTb> bioSampleSampleTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllByUploadNum(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(bioSampleSampleTwoResultTbList)) {
            bioSampleTestTwoResultTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
            bioSampleSampleTwoResultTbList.forEach(bioSampleSampleTwoResultTb -> {
                bioSampleSampleTwoResultDetailTbMapper.deleteByApplyNoAndSampleCode(bioSampleSampleTwoResultTb.getApplyNo(), bioSampleSampleTwoResultTb.getSampleCode());
            });
        }
        bioSampleTestOneResultTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
        bioSampleTestResultFileTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());

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
        BioSampleApplyTb bioSampleApplyTb = new BioSampleApplyTb();
        bioSampleApplyTb.setApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleApplyTb.setApplyNumber(applyNumber);
        bioSampleApplyTb.setApplyTime(new Date());
        bioSampleApplyTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        bioSampleApplyTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        bioSampleApplyTb.setApplyType(CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList()) ? SampleTestApplyTypeEnum.repeat.name() : SampleTestApplyTypeEnum.first.name());
        bioSampleApplyTb.setIdentifyExcelUrl(null);
        bioSampleApplyTb.setCloneFlag(newSampleTestDTO.isCloneFlag() ? BioDrQiContents.Y : BioDrQiContents.N);
        bioSampleApplyTb.setLayoutFlag(newSampleTestDTO.getTestType());
        bioSampleApplyTb.setVectorTaskCodes(null);
        bioSampleApplyTb.setSampleCodeRange(null);
        bioSampleApplyTbMapper.insert(bioSampleApplyTb);

        //重复取样申请
        if (CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList())) {
            List<BioSampleTestTb> targetBioSampleTestTbList = new ArrayList<>();
            //克隆苗
            for (NewSampleTestDTO.RepeatSampleApply repeatSampleApply : newSampleTestDTO.getRepeatSampleApplyList()) {
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCodeLike(repeatSampleApply.getSampleCode());
                List<Integer> sampleCodeSuffixList = bioSampleTestTbList.stream().filter(bioSampleTestTb -> bioSampleTestTb.getSampleCode().contains("-")).map(bioSampleTestTb -> Integer.valueOf(bioSampleTestTb.getSampleCode().substring(bioSampleTestTb.getSampleCode().indexOf("-") + 1))).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                int maxSampleCodeSuffix = CollectionUtil.isNotEmpty(sampleCodeSuffixList) ? Integer.valueOf(sampleCodeSuffixList.get(0)) : 0;
                for (int i = 1; i <= repeatSampleApply.getCloneSeedNum(); i++) {
                    BioSampleTestTb repeatBioSampleTestTb = new BioSampleTestTb();
                    repeatBioSampleTestTb.setVectorTaskCode(bioSampleTestTbList.get(0).getVectorTaskCode());
                    repeatBioSampleTestTb.setTransformCode(bioSampleTestTbList.get(0).getTransformCode());
                    repeatBioSampleTestTb.setSampleCode(repeatSampleApply.getSampleCode() + "-" + (maxSampleCodeSuffix + i));
                    repeatBioSampleTestTb.setApplyTime(new Date());
                    repeatBioSampleTestTb.setApplyUserId(SecurityContextHolder.getUserId());
                    repeatBioSampleTestTb.setApplyUserName(SecurityContextHolder.getNickName());
                    repeatBioSampleTestTb.setCreateTime(new Date());
                    repeatBioSampleTestTb.setApplyNo(bioSampleApplyTb.getApplyNo());
                    repeatBioSampleTestTb.setCloneSampleCode(repeatSampleApply.getSampleCode());
                    repeatBioSampleTestTb.setCheckResult(CheckResultEnum.noCheck.name());
                    repeatBioSampleTestTb.setUniqueCode(repeatBioSampleTestTb.getSampleCode());
                    repeatBioSampleTestTb.setSourceCode(SourceCodeEnum.project.name());
                    repeatBioSampleTestTb.setBreedCode(bioSampleTestTbList.get(0).getBreedCode());
                    repeatBioSampleTestTb.setSpeciesCode(bioSampleTestTbList.get(0).getSpeciesCode());
                    repeatBioSampleTestTb.setGeneration(bioSampleTestTbList.get(0).getGeneration());
                    repeatBioSampleTestTb.setTestResult(TestResultEnum.noTest.name());
                    targetBioSampleTestTbList.add(repeatBioSampleTestTb);
                }
            }

            try {
                bioSampleTestTbMapper.insertBatch(targetBioSampleTestTbList);
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
                List<BioSampleTestTb> targetBioSampleTestTbList = new ArrayList<>();
                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(firstSampleApply.getTransformCode(), firstSampleApply.getVectorTaskCode());
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerTransformTb.getVectorTaskCode());
                BioSampleCodePrefixTb bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                List<BioSampleTestTb> cerSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCodeLike(bioSampleCodePrefixTb.getSampleCodePrefix());
                Integer maxSampleNumber = null;
                if (CollectionUtil.isNotEmpty(cerSampleTestTbList)) {
                    cerSampleTestTbList = cerSampleTestTbList.stream().filter(cerSampleTestTb -> !cerSampleTestTb.getSampleCode().contains("-") && cerSampleTestTb.getSampleCode().startsWith(bioSampleCodePrefixTb.getSampleCodePrefix()) && LetterUtil.isNumeric(cerSampleTestTb.getSampleCode().substring(bioSampleCodePrefixTb.getSampleCodePrefix().length()))).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(cerSampleTestTbList)) {
                        maxSampleNumber = cerSampleTestTbList.stream().map(cerSampleTestTb -> Integer.valueOf(cerSampleTestTb.getSampleCode().substring(bioSampleCodePrefixTb.getSampleCodePrefix().length()))).max(Integer::compare).get();
                    }
                }
                for (int i = 1; i <= firstSampleApply.getSampleNum(); i++) {
                    maxSampleNumber = maxSampleNumber == null ? 1 : maxSampleNumber + 1;
                    BioSampleTestTb bioSampleTestTb = new BioSampleTestTb();
                    bioSampleTestTb.setVectorTaskCode(cerTransformTb.getVectorTaskCode());
                    bioSampleTestTb.setTransformCode(cerTransformTb.getTransformCode());
                    bioSampleTestTb.setSampleCode(bioSampleCodePrefixTb.getSampleCodePrefix() + maxSampleNumber);
                    bioSampleTestTb.setApplyTime(new Date());
                    bioSampleTestTb.setApplyUserId(SecurityContextHolder.getUserId());
                    bioSampleTestTb.setApplyUserName(SecurityContextHolder.getNickName());
                    bioSampleTestTb.setCreateTime(new Date());
                    bioSampleTestTb.setApplyNo(bioSampleApplyTb.getApplyNo());
                    bioSampleTestTb.setUniqueCode(bioSampleTestTb.getSampleCode());
                    bioSampleTestTb.setCheckResult(CheckResultEnum.noCheck.name());
                    bioSampleTestTb.setSourceCode(SourceCodeEnum.project.name());
                    bioSampleTestTb.setBreedCode(cerTransformTb.getBreedCode());
                    bioSampleTestTb.setSpeciesCode(cerTransformTb.getSpeciesCode());
                    bioSampleTestTb.setGeneration(GenerationEnum.T0.code);
                    bioSampleTestTb.setTestResult(TestResultEnum.noTest.name());
                    targetBioSampleTestTbList.add(bioSampleTestTb);
                }
                logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.sample_and_test, bioTaskDtlTb.getTaskNum());
                try {
                    bioSampleTestTbMapper.insertBatch(targetBioSampleTestTbList);
                } catch (DuplicateKeyException e) {
                    log.error("取样申请异常", e);
                    throw new BusinessException("取样编号有重复");
                }
            }
        }
        //如果是首次申请，更新申请中包含的事实方案和取样编号范围
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
        Map<String, List<BioSampleTestTb>> cerSampleTestTbListMap = bioSampleTestTbList.stream().collect(Collectors.groupingBy(BioSampleTestTb::getVectorTaskCode));
        bioSampleApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(cerSampleTestTbListMap.keySet()).replace("[", "").replace("]", "").replace("\"", ""));
        StringBuffer sampleCodeRangeBuff = new StringBuffer();
        if (SampleTestApplyTypeEnum.first.name().equals(bioSampleApplyTb.getApplyType())) {
            cerSampleTestTbListMap.forEach((vectorTaskCode, sampleTestList) -> {
                BioSampleCodePrefixTb bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
                sampleTestList = sampleTestList.stream().filter(sampleTest -> sampleTest.getSampleCode().startsWith(bioSampleCodePrefixTb.getSampleCodePrefix())).sorted(Comparator.comparing(sampleTest -> Integer.valueOf(sampleTest.getSampleCode().substring(2)))).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(sampleTestList)) {
                    sampleCodeRangeBuff.append(sampleTestList.get(0).getSampleCode() + "-" + sampleTestList.get(sampleTestList.size() - 1).getSampleCode()).append(",");
                }
            });
            if (StringUtils.isNotEmpty(sampleCodeRangeBuff.toString())) {
                bioSampleApplyTb.setSampleCodeRange(sampleCodeRangeBuff.substring(0, sampleCodeRangeBuff.length() - 1));
            }
        }
        bioSampleApplyTbMapper.updateById(bioSampleApplyTb);


    }

    public static void main(String[] args) {
        System.out.println("KXYGHB0001".startsWith("XV"));
    }

}

