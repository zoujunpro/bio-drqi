package com.bio.drqi.manage.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.TransformDTO;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
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
import java.util.ArrayList;
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

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;


    @GetMapping("cleanBreed")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanBreed() {
        /**
         * 清晰品种
         */
        log.info("清洗品种字典开始");
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        for (CerBreedDict cerBreedDict : cerBreedDictList) {
            log.info("清洗品种字典:" + JSONUtil.toJsonStr(cerBreedDict));
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
            log.info("清洗种子库品种:" + JSONUtil.toJsonStr(seedStockTb));
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
            log.info("田测申请品种:" + JSONUtil.toJsonStr(tcExperimentTb));
            List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByExperimentNum(tcExperimentTb.getExperimentNum());
            for (TcExperimentDesignTb tcExperimentDesignTb : tcExperimentDesignTbList) {
                String breedCode = breedRemarkMap.get(tcExperimentTb.getSpeciesCode() + ":" + tcExperimentDesignTb.getBreedCode());
                if (StringUtils.isEmpty(breedCode)) {
                    throw new BusinessException("找不到品种：" + tcExperimentDesignTb.getBreedCode());
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
            log.info("田测取样检测品种:" + JSONUtil.toJsonStr(tcSampleTestTb));
            TcExperimentDesignTb tcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcSampleTestTb.getExperimentNum(), tcSampleTestTb.getRegionNum(), tcSampleTestTb.getSeedNum());
            if (tcExperimentDesignTb != null) {
                tcExperimentDesignTb.setBreedCode(tcExperimentDesignTb.getBreedCode());
                tcSampleTestTbMapper.updateById(tcSampleTestTb);
            }
        }
        log.info("田测取样检测品种清洗结束");

        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectSelective(null);
        for (TcPollinationTb tcPollinationTb : tcPollinationTbList) {
            log.info("田测取样检测品种清洗:" + JSONUtil.toJsonStr(tcPollinationTb));
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
        log.info("工单清洗开始");
        //工单清洗
        List<BioTaskDtlTb> tc_pollination_task_applyList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("tc_pollination_task_apply");
        if (CollectionUtil.isNotEmpty(tc_pollination_task_applyList)) {
            tc_pollination_task_applyList.forEach(bioTaskDtlTb -> {
                log.info("tc_pollination_task_apply:" + JSONUtil.toJsonStr(bioTaskDtlTb));
                TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
                if (CollectionUtil.isNotEmpty(tcPollinationTaskDTO.getTcPollinationExcelDTOList())) {
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
                            TcExperimentDesignTb matherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationTaskDTO.getExperimentNum(), pollinationExcelDTO.getMotherRegionNum(), pollinationExcelDTO.getMotherSeedNum());

                            if (matherTcExperimentDesignTb != null) {
                                pollinationExcelDTO.setMotherBreedCode(matherTcExperimentDesignTb.getBreedCode());
                            } else {
                                throw new BusinessException("授粉品种数据异常");
                            }
                        }
                    });
                    bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcPollinationTaskDTO));
                    bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
                }


            });
        }
        List<BioTaskDtlTb> tc_sample_test_task_apply_applyList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("tc_sample_test_task_apply");
        for (BioTaskDtlTb bioTaskDtlTb : tc_sample_test_task_apply_applyList) {
            log.info("tc_sample_test_task_apply:" + JSONUtil.toJsonStr(bioTaskDtlTb));
            TcSampleTestTaskDTO tcSampleTestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);
            if (CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getRepeatSampleApplyList())) {
                tcSampleTestTaskDTO.getRepeatSampleApplyList().forEach(repeatSampleApply -> {
                    TcExperimentDesignTb matherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcSampleTestTaskDTO.getExperimentNum(), repeatSampleApply.getRegionNum(), repeatSampleApply.getSeedNum());
                    if (matherTcExperimentDesignTb != null) {
                        repeatSampleApply.setBreedCode(matherTcExperimentDesignTb.getBreedCode());
                    } else {
                        throw new BusinessException("数据异常，找不到品种");
                    }

                });
            }
            if (CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getFirstSampleApplyList())) {
                tcSampleTestTaskDTO.getFirstSampleApplyList().forEach(firstSampleApply -> {
                    TcExperimentDesignTb matherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcSampleTestTaskDTO.getExperimentNum(), firstSampleApply.getRegionNum(), firstSampleApply.getSeedNum());
                    if (matherTcExperimentDesignTb != null) {
                        firstSampleApply.setBreedCode(matherTcExperimentDesignTb.getBreedCode());
                    } else {
                        throw new BusinessException("数据异常，找不到品种");
                    }
                });
            }
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcSampleTestTaskDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }

        log.info("工单清洗结束");


        log.info("整体洗结束，不可重复清洗");

        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("/cleanTcDesign")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult cleanTcDesign() {
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, String> breedRemarkMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getSpeciesCode() + ":" + cerBreedDict.getRemark(), cerBreedDict -> cerBreedDict.getBreedCode()));
        log.info("田测申请品种开始");
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectList(null);
        for (TcExperimentTb tcExperimentTb : tcExperimentTbList) {
            log.info("田测申请品种:" + JSONUtil.toJsonStr(tcExperimentTb));
            List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByExperimentNum(tcExperimentTb.getExperimentNum());
            for (TcExperimentDesignTb tcExperimentDesignTb : tcExperimentDesignTbList) {
                String breedCode = breedRemarkMap.get(tcExperimentTb.getSpeciesCode() + ":" + tcExperimentDesignTb.getBreedCode());
                if (StringUtils.isEmpty(breedCode)) {
                    throw new BusinessException("找不到品种：" + tcExperimentDesignTb.getBreedCode());
                } else {
                    tcExperimentDesignTb.setBreedCode(breedCode);
                }
                tcExperimentDesignTbMapper.updateById(tcExperimentDesignTb);
            }
        }
        log.info("田测申请品种结束");
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanVectorTaskBreed")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult cleanVectorTaskBreed() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectList(null);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, CerBreedDict> cerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getSpeciesCode() + "|" + cerBreedDict.getBreedName(), cerBreedDict -> cerBreedDict));
        for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
            log.info("cerVectorTaskTb={}", JSONUtil.toJsonStr(cerVectorTaskTb));
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(cerVectorTaskTb.getSpeciesCode());
            if (cerSpeciesConf == null) {
                throw new BusinessException("物种不存在");
            }
            List<CerBreedDict> currentCerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(cerSpeciesConf.getSpeciesCode());
            if (StringUtils.isEmpty(cerVectorTaskTb.getAcceptorMaterial())) {
                cerVectorTaskTb.setAcceptorMaterial(currentCerBreedDictList.get(0).getBreedCode());
            } else {
                String[] acceptorMaterialNameArr = cerVectorTaskTb.getAcceptorMaterial().split("\\|");
                StringBuffer acceptorMaterialCodeBuf = new StringBuffer("");
                for (String acceptorMaterialName : acceptorMaterialNameArr) {
                    CerBreedDict currentCerBreedDict = cerBreedDictMap.get(cerSpeciesConf.getSpeciesCode() + "|" + acceptorMaterialName);
                    if (currentCerBreedDict != null) {
                        acceptorMaterialCodeBuf.append(acceptorMaterialName).append("|");
                    }
                }
                if (StringUtils.isEmpty(acceptorMaterialCodeBuf)) {
                    cerVectorTaskTb.setAcceptorMaterial(currentCerBreedDictList.get(0).getBreedCode());
                } else {
                    cerVectorTaskTb.setAcceptorMaterial(acceptorMaterialCodeBuf.substring(0, acceptorMaterialCodeBuf.length() - 1));
                }
            }
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
        }
        return ResponseResult.getSuccess("ok");
    }


    public ResponseResult cleanVectorSeed() {
        List<BioTaskDtlTb> implementation_planList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("implementation_plan");
        for (BioTaskDtlTb bioTaskDtlTb : implementation_planList) {
            log.info("implementation_plan:" + JSONUtil.toJsonStr(bioTaskDtlTb));
            VectorTaskAddDTO vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(vectorTaskAddDTO.getAcceptorMaterial(), vectorTaskAddDTO.getSpeciesCode());
            if (cerBreedDict != null) {
                vectorTaskAddDTO.setAcceptorMaterial(cerBreedDict.getBreedCode());
                bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(vectorTaskAddDTO));
                bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
            }
        }


        List<BioTaskDtlTb> transformList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("transform");
        for (BioTaskDtlTb bioTaskDtlTb : transformList) {
            log.info("transform:" + JSONUtil.toJsonStr(bioTaskDtlTb));
            TransformDTO transformDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TransformDTO.class);
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(transformDTO.getVectorTaskCode());
            transformDTO.getContentList().forEach(content -> {
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(content.getAcceptorMaterial(), cerVectorTaskTb.getSpeciesCode());
                if (cerBreedDict != null) {
                    content.setAcceptorMaterial(cerBreedDict.getBreedCode());
                }
            });
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(transformDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }


        log.info("实施方案转化品种清洗开始");
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectList(null);
        for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
            log.info("实施方案转化品种清洗:" + JSONUtil.toJsonStr(cerVectorTaskTb));
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(cerVectorTaskTb.getAcceptorMaterial(), cerVectorTaskTb.getSpeciesCode());
            if (cerBreedDict != null) {
                cerVectorTaskTb.setAcceptorMaterial(cerBreedDict.getBreedCode());
            } else {
                throw new BusinessException("数据异常，请检查品种数据");
            }
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

            List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
            cerTransformTbList.forEach(cerTransformTb -> {
                cerTransformTb.setAcceptorMaterial(cerVectorTaskTb.getAcceptorMaterial());
                cerTransformTbMapper.updateById(cerTransformTb);
            });
        }
        log.info("实施方案转化品种清洗结束");

        return ResponseResult.getSuccess("ok");
    }
}
