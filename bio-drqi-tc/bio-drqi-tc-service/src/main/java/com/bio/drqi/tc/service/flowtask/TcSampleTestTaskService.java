package com.bio.drqi.tc.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
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

    @Resource
    private TcPollinationSingleNumTbMapper tcPollinationSingleNumTbMapper;

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
            //每一个小区的最大的大田取样编号后缀
            Map<String, Integer> reginofMaxSampleCodeNumberMap = queryReginOfMaxSampleCodeNumber(experimentNum);
            TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(experimentNum);
            //当前数据库中某一个试验方案取样编号后缀最大值
            Integer maxSampleNumber = tcSampleTestTbMapper.selectAllByExperimentNum(experimentNum).stream().map(tcSampleTestTb -> Integer.valueOf(tcSampleTestTb.getSampleCode().substring(3))).max(Integer::compare).get();
            for (int i = 0; i < tcSampleTestTaskDTO.getFirstSampleApplyList().size(); i++) {
                TcSampleTestTaskDTO.FirstSampleApply firstSampleApply = tcSampleTestTaskDTO.getFirstSampleApplyList().get(i);
                for (int j = 1; j <= firstSampleApply.getSampleNum(); j++) {
                    Integer nextTcSampleCodeNumber = reginofMaxSampleCodeNumberMap.get(firstSampleApply.getRegionNum()) == null ? 1 : reginofMaxSampleCodeNumberMap.get(firstSampleApply.getRegionNum()) + 1;
                    maxSampleNumber=maxSampleNumber==null?1:maxSampleNumber+1;
                    reginofMaxSampleCodeNumberMap.put(firstSampleApply.getRegionNum(), nextTcSampleCodeNumber);
                    TcSampleTestTb tcSampleTestTb = new TcSampleTestTb();
                    tcSampleTestTb.setExperimentNum(tcSampleTestApplyTb.getExperimentNum());
                    tcSampleTestTb.setRegionNum(firstSampleApply.getRegionNum());
                    tcSampleTestTb.setSeedNum(firstSampleApply.getSeedNum());
                    tcSampleTestTb.setProjectCode(firstSampleApply.getProjectCode());
                    tcSampleTestTb.setVectorTaskCode(firstSampleApply.getVectorTaskCode());
                    tcSampleTestTb.setSpeciesCode(firstSampleApply.getSpeciesCode());
                    tcSampleTestTb.setBreedCode(firstSampleApply.getBreedCode());
                    tcSampleTestTb.setTargetCharacter(firstSampleApply.getTargetCharacter());
                    tcSampleTestTb.setGenerationCode(firstSampleApply.getGenerationCode());
                    tcSampleTestTb.setTcGene(firstSampleApply.getTcGene());
                    tcSampleTestTb.setSampleCode(tcExperimentTb.getSampleCodePrefix() + maxSampleNumber);
                    tcSampleTestTb.setSampleTime(firstSampleApply.getSampleTime());
                    tcSampleTestTb.setSampleApplyNum(tcSampleTestApplyTb.getSampleApplyNum());
                    tcSampleTestTb.setTaskNum(tcSampleTestApplyTb.getTaskNum());
                    tcSampleTestTb.setApplyType(tcSampleTestApplyTb.getApplyType());
                    tcSampleTestTb.setUniqueCode(tcSampleTestTb.getSampleCode());
                    tcSampleTestTb.setTcSampleCode(firstSampleApply.getRegionNum() + StringUtils.padl(nextTcSampleCodeNumber.toString(), 3, '0'));
                    batchList.add(tcSampleTestTb);
                }
            }

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
                tcSampleTestTb.setBreedCode(repeatSampleApply.getBreedCode());
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

    private Map<String, Integer> queryReginOfMaxSampleCodeNumber(String experimentNum) {
        Map<String, Integer> reginofMaxSampleCodeNumberMap = new HashMap<>();
        Map<String,List<String>> reginofMaxSampleCodeListMap=new HashMap<>();
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllByExperimentNum(experimentNum);
        List<TcPollinationSingleNumTb> tcPollinationSingleNumTbList = tcPollinationSingleNumTbMapper.selectAllByExperimentNumOrderByIdDesc(experimentNum);
        if (CollectionUtil.isNotEmpty(tcSampleTestTbList)) {
            tcSampleTestTbList.forEach(tcSampleTestTb -> {
                if(reginofMaxSampleCodeListMap.get(tcSampleTestTb.getRegionNum())==null){
                    reginofMaxSampleCodeListMap.put(tcSampleTestTb.getRegionNum(),Arrays.asList(tcSampleTestTb.getTcSampleCode()));
                }else {
                    reginofMaxSampleCodeListMap.get(tcSampleTestTb.getRegionNum()).add(tcSampleTestTb.getTcSampleCode());
                }
            });
        }
        if (CollectionUtil.isNotEmpty(tcPollinationSingleNumTbList)) {
            tcPollinationSingleNumTbList.forEach(tcPollinationSingleNumTb -> {
                if(reginofMaxSampleCodeListMap.get(tcPollinationSingleNumTb.getRegionNum())==null){
                    reginofMaxSampleCodeListMap.put(tcPollinationSingleNumTb.getRegionNum(),Arrays.asList(tcPollinationSingleNumTb.getTcSingleNumber()));
                }else {
                    reginofMaxSampleCodeListMap.get(tcPollinationSingleNumTb.getRegionNum()).add(tcPollinationSingleNumTb.getTcSingleNumber());
                }
            });

        }
        reginofMaxSampleCodeListMap.forEach((reginNum, tcSampleCodeList) -> {
            reginofMaxSampleCodeNumberMap.put(reginNum, tcSampleCodeList.stream().distinct().map(tcSampleCode -> Integer.valueOf(tcSampleCode.substring(reginNum.length()))).max(Integer::compare).get());
        });
        return reginofMaxSampleCodeNumberMap;
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
