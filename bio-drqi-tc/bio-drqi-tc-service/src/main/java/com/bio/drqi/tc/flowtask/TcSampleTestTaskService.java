package com.bio.drqi.tc.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.*;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("tc_sample_test_task_apply")
public class TcSampleTestTaskService extends AbstractTcBaseTaskService {

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private BioSampleTestHisTbMapper bioSampleTestHisTbMapper;

    @Resource
    private BioSampleApplyTbMapper bioSampleApplyTbMapper;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;

    @Resource
    private BioSampleTestResultFileTbMapper bioSampleTestResultFileTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private BioSampleLayoutTbMapper bioSampleLayoutTbMapper;

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

        //取样备注上区分是单管还是孔板取样
        if ("one".equals(tcSampleTestTaskDTO.getTestType())) {
            bioTaskDtlTb.setTaskDesc(bioTaskDtlTb.getTaskDesc() + "(单管取样)");
        } else {
            bioTaskDtlTb.setTaskDesc(bioTaskDtlTb.getTaskDesc() + "(96孔板取样)");
        }

        //插入数据库
        synchronized (this) {
            BioSampleApplyTb bioSampleApplyTb = bioSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());
            if (bioSampleApplyTb == null) {
                synchronized (this) {
                    bathInsertData(bioTaskDtlTb, tcSampleTestTaskDTO, tcSampleTestTaskDTO.getExperimentNum());
                    //如果是单管，则直接默认生成模板
                }
            }
        }

    }

    private void bathInsertData(BioTaskDtlTb bioTaskDtlTb, TcSampleTestTaskDTO tcSampleTestTaskDTO, String experimentNum) {
        BioSampleApplyTb bioSampleApplyTb = new BioSampleApplyTb();
        bioSampleApplyTb.setApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleApplyTb.setApplyNumber(0);
        bioSampleApplyTb.setApplyTime(new Date());
        bioSampleApplyTb.setApplyUserId(SecurityContextHolder.getUserId());
        bioSampleApplyTb.setApplyUserName(SecurityContextHolder.getNickName());
        bioSampleApplyTb.setApplyType(tcSampleTestTaskDTO.getApplyType());
        bioSampleApplyTb.setLayoutFlag(tcSampleTestTaskDTO.getTestType());
        bioSampleApplyTb.setVectorTaskCodes(null);
        bioSampleApplyTb.setSampleCodeRange(null);
        bioSampleApplyTbMapper.insert(bioSampleApplyTb);

        List<BioSampleTestTb> batchList = new ArrayList<BioSampleTestTb>();
        List<TcPollinationSingleNumTb> tcPollinationSingleNumTbList = new ArrayList<TcPollinationSingleNumTb>();
        //首次取样
        if (CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getFirstSampleApplyList())) {
            //每一个小区的最大的大田取样编号后缀
            Map<String, Integer> reginofMaxSampleCodeNumberMap = queryReginOfMaxSampleCodeNumber(experimentNum);
            TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(experimentNum);
            //当前数据库中某一个试验方案取样编号后缀最大值
            List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByExperimentNum(experimentNum);
            Integer maxSampleNumber = null;
            if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
                maxSampleNumber = bioSampleTestTbList.stream().map(bioSampleTestTb -> Integer.valueOf(bioSampleTestTb.getSampleCode().substring(3))).max(Integer::compare).get();
            }
            for (int i = 0; i < tcSampleTestTaskDTO.getFirstSampleApplyList().size(); i++) {
                TcSampleTestTaskDTO.FirstSampleApply firstSampleApply = tcSampleTestTaskDTO.getFirstSampleApplyList().get(i);
                for (int j = 1; j <= firstSampleApply.getSampleNum(); j++) {
                    Integer nextTcSampleCodeNumber = reginofMaxSampleCodeNumberMap.get(firstSampleApply.getRegionNum()) == null ? 1 : reginofMaxSampleCodeNumberMap.get(firstSampleApply.getRegionNum()) + 1;
                    maxSampleNumber = maxSampleNumber == null ? 1 : maxSampleNumber + 1;
                    reginofMaxSampleCodeNumberMap.put(firstSampleApply.getRegionNum(), nextTcSampleCodeNumber);
                    BioSampleTestTb bioSampleTestTb = new BioSampleTestTb();
                    bioSampleTestTb.setVectorTaskCode(firstSampleApply.getVectorTaskCode());
                    bioSampleTestTb.setSampleCode(tcExperimentTb.getSampleCodePrefix() + maxSampleNumber);
                    bioSampleTestTb.setApplyTime(new Date());
                    bioSampleTestTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
                    bioSampleTestTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
                    bioSampleTestTb.setCreateTime(new Date());
                    bioSampleTestTb.setApplyNo(bioSampleApplyTb.getApplyNo());
                    bioSampleTestTb.setUniqueCode(bioSampleTestTb.getSampleCode());
                    bioSampleTestTb.setCheckResult(CheckResultEnum.noCheck.name());
                    bioSampleTestTb.setSourceCode(SourceCodeEnum.field.name());
                    bioSampleTestTb.setBreedCode(firstSampleApply.getBreedCode());
                    bioSampleTestTb.setSpeciesCode(firstSampleApply.getSpeciesCode());
                    bioSampleTestTb.setGeneration(GenerationEnum.T0.code);
                    bioSampleTestTb.setRegionNum(firstSampleApply.getRegionNum());
                    bioSampleTestTb.setSeedNum(firstSampleApply.getSeedNum());
                    bioSampleTestTb.setExperimentNum(experimentNum);
                    bioSampleTestTb.setTestResult(TestResultEnum.noTest.name());
                    bioSampleTestTb.setApplyTime(new Date());
                    batchList.add(bioSampleTestTb);

                    TcPollinationSingleNumTb tcPollinationSingleNumTb = TcPollinationSingleNumTb.of(experimentNum, bioSampleTestTb.getSeedNum(), bioSampleTestTb.getRegionNum(), firstSampleApply.getRegionNum() + StringUtils.padl(nextTcSampleCodeNumber.toString(), 3, '0'), bioSampleTestTb.getSampleCode(), bioSampleTestTb.getApplyNo(), bioSampleTestTb.getApplyUserName());
                    tcPollinationSingleNumTbList.add(tcPollinationSingleNumTb);

                }
            }

        }
        //重复取样
        if (CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getRepeatSampleApplyList())) {
            for (TcSampleTestTaskDTO.RepeatSampleApply repeatSampleApply : tcSampleTestTaskDTO.getRepeatSampleApplyList()) {
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(repeatSampleApply.getSampleCode());
                if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
                    throw new BusinessException("没找到历史取样信息" + repeatSampleApply.getSampleCode());
                }
                BioSampleTestTb bioSampleTestTb = BioSampleTestTb.ofRepeat(bioSampleTestTbList.get(0), bioTaskDtlTb, CheckResultEnum.noCheck, TestResultEnum.noTest);
                batchList.add(bioSampleTestTb);
            }
        }

        if (CollectionUtil.isNotEmpty(tcPollinationSingleNumTbList)) {
            tcPollinationSingleNumTbMapper.insertBatch(tcPollinationSingleNumTbList);
        }
        bioSampleTestTbMapper.insertBatch(batchList);
    }

    private Map<String, Integer> queryReginOfMaxSampleCodeNumber(String experimentNum) {
        Map<String, Integer> reginofMaxSampleCodeNumberMap = new HashMap<>();
        Map<String, List<String>> reginofMaxSampleCodeListMap = new HashMap<>();
        List<TcPollinationSingleNumTb> tcPollinationSingleNumTbList = tcPollinationSingleNumTbMapper.selectAllByExperimentNumOrderByIdDesc(experimentNum);
        if (CollectionUtil.isNotEmpty(tcPollinationSingleNumTbList)) {
            tcPollinationSingleNumTbList.forEach(tcPollinationSingleNumTb -> {
                if (reginofMaxSampleCodeListMap.get(tcPollinationSingleNumTb.getRegionNum()) == null) {
                    reginofMaxSampleCodeListMap.put(tcPollinationSingleNumTb.getRegionNum(), Arrays.asList(tcPollinationSingleNumTb.getTcSingleNumber()));
                } else {
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
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            BioSampleApplyTb bioSampleApplyTb = bioSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());
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
                bioSampleTestTwoResultDetailTbMapper.deleteByApplyNoAndSampleCode(bioSampleSampleTwoResultTb.getApplyNo(), bioSampleSampleTwoResultTb.getSampleCode());
            });
        }
        bioSampleTestResultFileTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
        bioSampleTestOneResultTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
        tcPollinationSingleNumTbMapper.deleteBySampleApplyNum(bioTaskDtlTb.getTaskNum());
    }
}
