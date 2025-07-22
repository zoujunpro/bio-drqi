package com.bio.drqi.manage.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import com.bio.drqi.tc.service.dto.TcPollinationTaskDTO;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/testClean")
@Slf4j
public class Clean20250721Controller {

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;


    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;


    @Value("${spring.profiles.active}")
    private String active;


    @GetMapping("cleanBreed")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanBreed() {
        /**
         * 清晰品种
         */
        log.info("清洗品种字典开始");
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        for (CerBreedDict cerBreedDict : cerBreedDictList) {
            cerBreedDict.setRemark(cerBreedDict.getBreedCode());
            cerBreedDict.setBreedCode(IdUtils.simpleUUID());
            cerBreedDictMapper.updateById(cerBreedDict);
        }
        log.info("清洗品种字典结束");
        log.info("清洗种子库品种开始");
        Map<String, String> breedRemarkMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getSpeciesCode() + ":" + cerBreedDict.getRemark(), cerBreedDict -> cerBreedDict.getBreedCode()));
        Map<String, String> breedNameMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getSpeciesCode() + ":" + cerBreedDict.getBreedName(), cerBreedDict -> cerBreedDict.getBreedCode()));

        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectList(null);
        for (SeedStockTb seedStockTb : seedStockTbList) {
            String breedCode = breedRemarkMap.get(seedStockTb.getSpeciesCode() + ":" + seedStockTb.getBreedCode());
            if (StringUtils.isEmpty(breedCode)) {
                throw new BusinessException("找不到品种");
            }
            seedStockTb.setBreedCode(breedCode);
            seedStockTbMapper.updateById(seedStockTb);
        }
        log.info("清洗种子库品种开始");
        //田测申请清洗
        log.info("田测申请品种开始");
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectList(null);
        for (TcExperimentTb tcExperimentTb : tcExperimentTbList) {
            List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByExperimentNum(tcExperimentTb.getExperimentNum());
            List<CerBreedDict> breedDictList = cerBreedDictMapper.selectAllBySpeciesCode(tcExperimentTb.getSpeciesCode());
            for (TcExperimentDesignTb tcExperimentDesignTb : tcExperimentDesignTbList) {
                String breedCode = breedNameMap.get(tcExperimentTb.getSpeciesCode() + ":" + tcExperimentDesignTb.getBreedCode());
                if (StringUtils.isEmpty(breedNameMap)) {
                    if ("prod".equals(active)) {
                        throw new BusinessException("找不到品种：" + tcExperimentDesignTb.getBreedCode());
                    } else {
                        tcExperimentDesignTb.setBreedCode(breedDictList.get(0).getBreedCode());
                    }
                } else {
                    tcExperimentDesignTb.setBreedCode(breedCode);
                }
                tcExperimentDesignTbMapper.updateById(tcExperimentDesignTb);
            }
        }
        log.info("田测申请品种结束");
        log.info("田测取样检测品种清洗开始");
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectList(null);
        for (TcSampleTestTb tcSampleTestTb : tcSampleTestTbList) {
            TcExperimentDesignTb tcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcSampleTestTb.getExperimentNum(), tcSampleTestTb.getRegionNum(), tcSampleTestTb.getSeedNum());
            if (tcExperimentDesignTb != null) {
                tcExperimentDesignTb.setBreedCode(tcExperimentDesignTb.getBreedCode());
                tcSampleTestTbMapper.updateById(tcSampleTestTb);
            }
        }
        log.info("田测取样检测品种清洗结束");

        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectSelective(null);
        for (TcPollinationTb tcPollinationTb : tcPollinationTbList) {
            if (tcPollinationTb.getFRegionNum() != null && tcPollinationTb.getFSeedNum() != null) {
                TcExperimentDesignTb fatherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationTb.getExperimentNum(), tcPollinationTb.getFRegionNum(), tcPollinationTb.getFSeedNum());
                if (fatherTcExperimentDesignTb != null) {
                    tcPollinationTb.setFBreedCode(fatherTcExperimentDesignTb.getBreedCode());
                } else {
                    throw new BusinessException("授粉品种数据异常");
                }
            }
            if (tcPollinationTb.getMRegionNum() != null && tcPollinationTb.getMSeedNum() != null) {
                TcExperimentDesignTb matherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationTb.getExperimentNum(), tcPollinationTb.getMRegionNum(), tcPollinationTb.getMSeedNum());

                if (matherTcExperimentDesignTb != null) {
                    tcPollinationTb.setMBreedCode(matherTcExperimentDesignTb.getBreedCode());
                } else {
                    throw new BusinessException("授粉品种数据异常");
                }
            }
            tcPollinationTbMapper.updateById(tcPollinationTb);
        }

        //工单清洗
        List<BioTaskDtlTb> tc_pollination_task_applyList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("tc_pollination_task_apply");
        if(CollectionUtil.isNotEmpty(tc_pollination_task_applyList)){
            tc_pollination_task_applyList.forEach(bioTaskDtlTb -> {
                TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
                tcPollinationTaskDTO.getTcPollinationExcelDTOList().forEach(pollinationExcelDTO -> {
                    if (pollinationExcelDTO.getFatherRegionNum() != null && pollinationExcelDTO.getFatherSeedNum() != null) {
                        TcExperimentDesignTb fatherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationTaskDTO.getExperimentNum(), pollinationExcelDTO.getFatherRegionNum(), pollinationExcelDTO.getFatherSeedNum());
                        if (fatherTcExperimentDesignTb != null) {
                            pollinationExcelDTO.setFatherBreedCode(fatherTcExperimentDesignTb.getBreedCode());
                        } else {
                            throw new BusinessException("授粉品种数据异常");
                        }
                    }
                    if (pollinationExcelDTO.getMotherRegionNum() != null && pollinationExcelDTO.getMotherSeedNum() != null) {
                        TcExperimentDesignTb matherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationTaskDTO.getExperimentNum(), pollinationExcelDTO.getMotherRegionNum(), pollinationExcelDTO.getMotherSeedNum() );

                        if (matherTcExperimentDesignTb != null) {
                            pollinationExcelDTO.setMotherBreedCode(matherTcExperimentDesignTb.getBreedCode());
                        } else {
                            throw new BusinessException("授粉品种数据异常");
                        }
                    }
                });
                bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcPollinationTaskDTO));
                bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
            });

            List<BioTaskDtlTb> tc_sample_test_task_apply_applyList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("tc_sample_test_task_apply");
            for (BioTaskDtlTb bioTaskDtlTb:tc_sample_test_task_apply_applyList){
                TcSampleTestTaskDTO tcSampleTestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);
                if(CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getRepeatSampleApplyList())){
                    tcSampleTestTaskDTO.getRepeatSampleApplyList().forEach(repeatSampleApply->{
                        TcExperimentDesignTb matherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcSampleTestTaskDTO.getExperimentNum(), repeatSampleApply.getRegionNum(), repeatSampleApply.getSeedNum() );
                        if(matherTcExperimentDesignTb!=null){
                            repeatSampleApply.setBreedCode(matherTcExperimentDesignTb.getBreedCode());
                        }else {
                            throw new BusinessException("数据异常，找不到品种");
                        }

                    });
                }
                if(CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getFirstSampleApplyList())){
                    tcSampleTestTaskDTO.getFirstSampleApplyList().forEach(firstSampleApply -> {
                        TcExperimentDesignTb matherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcSampleTestTaskDTO.getExperimentNum(), firstSampleApply.getRegionNum(), firstSampleApply.getSeedNum() );
                        if(matherTcExperimentDesignTb!=null){
                            firstSampleApply.setBreedCode(matherTcExperimentDesignTb.getBreedCode());
                        }else {
                            throw new BusinessException("数据异常，找不到品种");
                        }
                    });
                }
                bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcSampleTestTaskDTO));
                bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
            }

        }

        return ResponseResult.getSuccess("ok");


    }
}
