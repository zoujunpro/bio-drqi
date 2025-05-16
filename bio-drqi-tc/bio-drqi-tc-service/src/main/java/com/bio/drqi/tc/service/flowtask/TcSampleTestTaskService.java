package com.bio.drqi.tc.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.CerSampleCodePrefixTbMapper;
import com.bio.drqi.mapper.TcSampleTestApplyTbMapper;
import com.bio.drqi.mapper.TcSampleTestTbMapper;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("tc_sample_test_task_apply")
public class TcSampleTestTaskService extends AbstractTcBaseTaskService {

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private TcSampleTestApplyTbMapper tcSampleTestApplyTbMapper;

    @Resource
    private CerSampleCodePrefixTbMapper cerSampleCodePrefixTbMapper;

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
        //校验

        //插入数据库
        synchronized (this) {
            TcSampleTestApplyTb tcSampleTestApplyTb = tcSampleTestApplyTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
            if (tcSampleTestApplyTb == null) {
                synchronized (this) {
                    bathInsertData(bioTaskDtlTb, tcSampleTestTaskDTO);
                    //如果是单管，则直接默认生成模板
                }
            }
        }

    }

    private void bathInsertData(BioTaskDtlTb bioTaskDtlTb, TcSampleTestTaskDTO tcSampleTestTaskDTO) {
        TcSampleTestApplyTb tcSampleTestApplyTb = new TcSampleTestApplyTb();
        tcSampleTestApplyTb.setSampleApplyNum(bioTaskDtlTb.getTaskNum());
        tcSampleTestApplyTb.setExperimentNum(tcSampleTestTaskDTO.getExperimentCode());
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
            for (TcSampleTestTaskDTO.FirstSampleApply firstSampleApply : tcSampleTestTaskDTO.getFirstSampleApplyList()) {
                CerSampleCodePrefixTb cerSampleCodePrefixTb = cerSampleCodePrefixTbMapper.selectOneByVectorTaskCode(firstSampleApply.getVectorTaskCode());
                for (int i = 1; i <= firstSampleApply.getSampleNum(); i++) {
                    TcSampleTestTb tcSampleTestTb = new TcSampleTestTb();
                    tcSampleTestTb.setExperimentNum(tcSampleTestApplyTb.getExperimentNum());
                    tcSampleTestTb.setRegionNum(firstSampleApply.getRegionNum());
                    tcSampleTestTb.setSeedNum(firstSampleApply.getSeedNum());
                    tcSampleTestTb.setProjectCode(firstSampleApply.getProjectCode());
                    tcSampleTestTb.setVectorTaskCode(firstSampleApply.getVectorTaskCode());
                    tcSampleTestTb.setSpeciesCode(firstSampleApply.getSpeciesCode());
                    tcSampleTestTb.setTargetCharacter(firstSampleApply.getTargetCharacter());
                    tcSampleTestTb.setGenerationCode(firstSampleApply.getGenerationCode());
                    tcSampleTestTb.setTcGene(firstSampleApply.getTcGene());
                    tcSampleTestTb.setSampleCode(cerSampleCodePrefixTb+ DateUtil.format(new Date(),"HHmmss")+i);
                    tcSampleTestTb.setSampleTime(firstSampleApply.getSampleTime());
                    tcSampleTestTb.setSampleApplyNum(tcSampleTestApplyTb.getSampleApplyNum());
                    tcSampleTestTb.setTaskNum(tcSampleTestApplyTb.getTaskNum());
                    tcSampleTestTb.setApplyType(tcSampleTestApplyTb.getApplyType());
                    tcSampleTestTb.setUniqueCode(tcSampleTestTb.getSampleCode());
                    batchList.add(tcSampleTestTb);
                }

            }
        }
        //重复取样
        if (CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getRepeatSampleApplyList())) {
            for (TcSampleTestTaskDTO.RepeatSampleApply repeatSampleApply : tcSampleTestTaskDTO.getRepeatSampleApplyList()) {
                TcSampleTestTb tcSampleTestTb = new TcSampleTestTb();
                tcSampleTestTb.setExperimentNum(tcSampleTestApplyTb.getExperimentNum());
                tcSampleTestTb.setRegionNum(repeatSampleApply.getRegionNum());
                tcSampleTestTb.setSeedNum(repeatSampleApply.getSeedNum());
                tcSampleTestTb.setProjectCode(repeatSampleApply.getProjectCode());
                tcSampleTestTb.setVectorTaskCode(repeatSampleApply.getVectorTaskCode());
                tcSampleTestTb.setSpeciesCode(repeatSampleApply.getSpeciesCode());
                tcSampleTestTb.setTargetCharacter(repeatSampleApply.getTargetCharacter());
                tcSampleTestTb.setGenerationCode(repeatSampleApply.getGenerationCode());
                tcSampleTestTb.setTcGene(repeatSampleApply.getTcGene());
                tcSampleTestTb.setSampleCode(repeatSampleApply.getSampleCode());
                tcSampleTestTb.setSampleApplyNum(tcSampleTestApplyTb.getSampleApplyNum());
                tcSampleTestTb.setTaskNum(tcSampleTestApplyTb.getTaskNum());
                tcSampleTestTb.setApplyType(tcSampleTestApplyTb.getApplyType());
                tcSampleTestTb.setSampleTime(repeatSampleApply.getSampleTime());
                batchList.add(tcSampleTestTb);
            }
        }
        tcSampleTestTbMapper.insertBatch(batchList);
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
