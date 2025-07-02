package com.bio.drqi.tc.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.TcExperimentTb;
import com.bio.drqi.domain.TcSampleTestApplyTb;
import com.bio.drqi.domain.TcSampleTestTb;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.enums.SampleTestTypeEnum;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("tc_sample_test_task_apply")
public class TcSampleTestTaskService extends AbstractTcBaseTaskService {

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private TcSampleTestApplyTbMapper tcSampleTestApplyTbMapper;

    @Resource
    private TcSampleTestBioResultRefMapper tcSampleTestBioResultRefMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcSampleLayoutTbMapper tcSampleLayoutTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcSampleTestTaskDTO tcSampleTestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);
        if (tcSampleTestTaskDTO == null) {
            throw new BusinessException("工单无表单信息");
        }
        ValidatorUtil.validator(tcSampleTestTaskDTO);

        if (StringUtils.isEmpty(tcSampleTestTaskDTO.getFirstSampleApplyList()) && StringUtils.isEmpty(tcSampleTestTaskDTO.getRepeatSampleApplyList())) {
            throw new BusinessException("缺少取样数据");
        }

        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcSampleTestTaskDTO.getExperimentNum());
        if (tcExperimentTb == null) {
            throw new BusinessException("不存在此试验");
        }

        if (!ExperimentStatusEnum.INIT.status.equals(tcExperimentTb.getExperimentStatus())) {
            throw new BusinessException("非进行中试验，无法进行任何操作");
        }

        //插入数据库
        synchronized (this) {
            TcSampleTestApplyTb tcSampleTestApplyTb = tcSampleTestApplyTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
            if (tcSampleTestApplyTb == null) {
                synchronized (this) {
                    bathInsertData(bioTaskDtlTb, tcSampleTestTaskDTO, tcSampleTestTaskDTO.getExperimentNum());
                    //如果是单管，则直接默认生成模板
                }
            }
        }

    }

    private void bathInsertData(BioTaskDtlTb bioTaskDtlTb, TcSampleTestTaskDTO tcSampleTestTaskDTO, String experimentNum) {
        TcSampleTestApplyTb tcSampleTestApplyTb = new TcSampleTestApplyTb();
        tcSampleTestApplyTb.setSampleApplyNum(bioTaskDtlTb.getTaskNum());
        tcSampleTestApplyTb.setExperimentNum(tcSampleTestTaskDTO.getExperimentNum());
        tcSampleTestApplyTb.setTaskNum(bioTaskDtlTb.getTaskNum());
        tcSampleTestApplyTb.setSampleOrganize(tcSampleTestTaskDTO.getSampleOrganize());
        tcSampleTestApplyTb.setApplyType(tcSampleTestTaskDTO.getApplyType());
        tcSampleTestApplyTb.setTestType(tcSampleTestTaskDTO.getTestType());
        tcSampleTestApplyTb.setExpectedSampleTime(tcSampleTestTaskDTO.getExpectedSampleTime());
        tcSampleTestApplyTb.setExpectedResultTime(tcSampleTestTaskDTO.getExpectedResultTime());
        tcSampleTestApplyTb.setCreateUserId(SecurityContextHolder.getUserId());
        tcSampleTestApplyTb.setCreateUserName(SecurityContextHolder.getNickName());
        tcSampleTestApplyTb.setCreateTime(new Date());
        tcSampleTestApplyTbMapper.insert(tcSampleTestApplyTb);

        List<TcSampleTestTb> batchList = new ArrayList<TcSampleTestTb>();
        //首次取样
        if (CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getFirstSampleApplyList())) {
            Map<String, Integer> reginofMaxSampleCodeMap = queryReginOfSampleCode(experimentNum);
            TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(experimentNum);
            Integer nextSampleNumber = tcExperimentTb.getNextSampleNumber();
            for (int i = 0; i < tcSampleTestTaskDTO.getFirstSampleApplyList().size(); i++) {
                TcSampleTestTaskDTO.FirstSampleApply firstSampleApply = tcSampleTestTaskDTO.getFirstSampleApplyList().get(i);
                for (int j = 1; j <= firstSampleApply.getSampleNum(); j++) {
                    Integer nextTcSampleCodeNumber = reginofMaxSampleCodeMap.get(firstSampleApply.getRegionNum()) == null ? 1 : reginofMaxSampleCodeMap.get(firstSampleApply.getRegionNum()) + 1;
                    reginofMaxSampleCodeMap.put(firstSampleApply.getRegionNum(), nextTcSampleCodeNumber);
                    TcSampleTestTb tcSampleTestTb = new TcSampleTestTb();
                    tcSampleTestTb.setExperimentNum(tcSampleTestApplyTb.getExperimentNum());
                    tcSampleTestTb.setRegionNum(firstSampleApply.getRegionNum());
                    tcSampleTestTb.setSeedNum(firstSampleApply.getSeedNum());
                    tcSampleTestTb.setProjectCode(firstSampleApply.getProjectCode());
                    tcSampleTestTb.setVectorTaskCode(firstSampleApply.getVectorTaskCode());
                    tcSampleTestTb.setSpeciesCode(firstSampleApply.getSpeciesCode());
                    tcSampleTestTb.setBreedName(firstSampleApply.getBreedName());
                    tcSampleTestTb.setTargetCharacter(firstSampleApply.getTargetCharacter());
                    tcSampleTestTb.setGenerationCode(firstSampleApply.getGenerationCode());
                    tcSampleTestTb.setTcGene(firstSampleApply.getTcGene());
                    tcSampleTestTb.setSampleCode(tcExperimentTb.getSampleCodePrefix() + nextSampleNumber);
                    tcSampleTestTb.setSampleTime(firstSampleApply.getSampleTime());
                    tcSampleTestTb.setSampleApplyNum(tcSampleTestApplyTb.getSampleApplyNum());
                    tcSampleTestTb.setTaskNum(tcSampleTestApplyTb.getTaskNum());
                    tcSampleTestTb.setApplyType(tcSampleTestApplyTb.getApplyType());
                    tcSampleTestTb.setUniqueCode(tcSampleTestTb.getSampleCode());
                    tcSampleTestTb.setTcSampleCode(firstSampleApply.getRegionNum() + StringUtils.padl(nextTcSampleCodeNumber.toString(), 3, '0'));
                    batchList.add(tcSampleTestTb);

                    //算出下次取样编号
                    nextSampleNumber = nextSampleNumber + 1;
                }

            }
            tcExperimentTb.setNextSampleNumber(nextSampleNumber);
            tcExperimentTbMapper.updateById(tcExperimentTb);
        }
        //重复取样
        if (CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getRepeatSampleApplyList())) {
            for (TcSampleTestTaskDTO.RepeatSampleApply repeatSampleApply : tcSampleTestTaskDTO.getRepeatSampleApplyList()) {
                TcSampleTestTb orgTcSampleTestTb = tcSampleTestTbMapper.selectOneByUniqueCode(repeatSampleApply.getSampleCode());
                if (orgTcSampleTestTb == null) {
                    throw new BusinessException("没找到历史取样信息" + repeatSampleApply.getSampleCode());
                }
                TcSampleTestTb tcSampleTestTb = new TcSampleTestTb();
                tcSampleTestTb.setExperimentNum(tcSampleTestApplyTb.getExperimentNum());
                tcSampleTestTb.setRegionNum(repeatSampleApply.getRegionNum());
                tcSampleTestTb.setSeedNum(repeatSampleApply.getSeedNum());
                tcSampleTestTb.setProjectCode(repeatSampleApply.getProjectCode());
                tcSampleTestTb.setVectorTaskCode(repeatSampleApply.getVectorTaskCode());
                tcSampleTestTb.setSpeciesCode(repeatSampleApply.getSpeciesCode());
                tcSampleTestTb.setBreedName(repeatSampleApply.getBreedName());
                tcSampleTestTb.setTargetCharacter(repeatSampleApply.getTargetCharacter());
                tcSampleTestTb.setGenerationCode(repeatSampleApply.getGenerationCode());
                tcSampleTestTb.setTcGene(repeatSampleApply.getTcGene());
                tcSampleTestTb.setSampleCode(repeatSampleApply.getSampleCode());
                tcSampleTestTb.setSampleApplyNum(tcSampleTestApplyTb.getSampleApplyNum());
                tcSampleTestTb.setTaskNum(tcSampleTestApplyTb.getTaskNum());
                tcSampleTestTb.setApplyType(tcSampleTestApplyTb.getApplyType());
                tcSampleTestTb.setSampleTime(repeatSampleApply.getSampleTime());
                tcSampleTestTb.setTcSampleCode(orgTcSampleTestTb.getTcSampleCode());
                batchList.add(tcSampleTestTb);
            }
        }
        tcSampleTestTbMapper.insertBatch(batchList);
    }

    private Map<String, Integer> queryReginOfSampleCode(String experimentNum) {
        Map<String, Integer> reginofMaxSampleCodeMap = new HashMap<>();
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllByExperimentNum(experimentNum);
        if (CollectionUtil.isNotEmpty(tcSampleTestTbList)) {
            Map<String, List<TcSampleTestTb>> mapList = tcSampleTestTbList.stream().collect(Collectors.groupingBy(TcSampleTestTb::getRegionNum));
            mapList.forEach((reginNum, sampletestList) -> {
                reginofMaxSampleCodeMap.put(reginNum, sampletestList.stream().map(tcSampleTestTb -> Integer.valueOf(tcSampleTestTb.getTcSampleCode().substring(reginNum.length()))).max(Integer::compare).get());
            });
        }
        return reginofMaxSampleCodeMap;
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcSampleTestTaskDTO tcSampleTestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            TcSampleTestApplyTb tcSampleTestApplyTb = tcSampleTestApplyTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
            tcSampleTestApplyTb.setIdentifyPrimerExcelUrl(tcSampleTestTaskDTO.getIdentifyPrimerTemplateExcelUrl());
            tcSampleTestApplyTb.setNgsResultExcelUrl(tcSampleTestTaskDTO.getBioInfoResultExcelUrl());
            tcSampleTestApplyTb.setOneResultExcelUrl(tcSampleTestTaskDTO.getTestDataExcelUrl());
            tcSampleTestApplyTbMapper.updateById(tcSampleTestApplyTb);

        }


    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        TcSampleTestApplyTb tcSampleTestApplyTb = tcSampleTestApplyTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
        tcSampleTestApplyTbMapper.deleteBySampleApplyNum(bioTaskDtlTb.getTaskNum());
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNum(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(tcSampleTestTbList)) {
            Optional<Integer> minSampleCodeOptional = tcSampleTestTbList.stream().map(tcSampleTestTb -> Integer.valueOf(tcSampleTestTb.getSampleCode().substring(3))).min(Integer::compare);
            if (minSampleCodeOptional.isPresent()) {
                TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcSampleTestApplyTb.getExperimentNum());
                tcExperimentTb.setNextSampleNumber(minSampleCodeOptional.get());
                tcExperimentTbMapper.updateById(tcExperimentTb);
            }
        }
        tcSampleTestTbMapper.deleteBySampleApplyNum(bioTaskDtlTb.getTaskNum());
        tcSampleTestBioResultRefMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        tcSampleTestBioResultRefMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        tcSampleLayoutTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
    }
}
