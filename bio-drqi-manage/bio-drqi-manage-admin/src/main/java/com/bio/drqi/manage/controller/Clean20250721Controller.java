package com.bio.drqi.manage.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.TransformDTO;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import com.bio.drqi.tc.service.dto.TcPollinationTaskDTO;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import com.lark.oapi.service.task.v1.enums.SourceEnum;
import lombok.Data;
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


    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;


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
                if ("4CV".equals(tcExperimentDesignTb.getBreedCode())) {
                    tcExperimentDesignTb.setBreedCode("XY335F");
                }
                if ("6WC".equals(tcExperimentDesignTb.getBreedCode())) {
                    tcExperimentDesignTb.setBreedCode("XY335M");
                }
                if ("中黄13".equals(tcExperimentDesignTb.getBreedCode())) {
                    tcExperimentDesignTb.setBreedCode("ZH13");
                }
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
        log.info("田测申请品种开始");
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectList(null);
        for (TcExperimentTb tcExperimentTb : tcExperimentTbList) {
            log.info("田测申请品种:" + JSONUtil.toJsonStr(tcExperimentTb));
            List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByExperimentNum(tcExperimentTb.getExperimentNum());
            for (TcExperimentDesignTb tcExperimentDesignTb : tcExperimentDesignTbList) {
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(tcExperimentDesignTb.getBreedCode());
                if (cerBreedDict == null) {
                    throw new BusinessException("找不到品种：" + tcExperimentDesignTb.getBreedCode());
                } else {
                    tcExperimentDesignTb.setBreedCode(cerBreedDict.getBreedCode());
                }
                tcExperimentDesignTbMapper.updateById(tcExperimentDesignTb);
            }
        }
        log.info("田测申请品种结束");
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("cleanVectorTaskBreedStep1")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult modifyVectorTaskBreed() {
        List<VectorTaskExcel> vectorTaskExcelList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\受体品种确认-new(1)LWR.xlsx", VectorTaskExcel.class);

        for (VectorTaskExcel vectorTaskExcel : vectorTaskExcelList) {
            log.info("vectorTaskExcel={}", JSONUtil.toJsonStr(vectorTaskExcel));
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(vectorTaskExcel.id);
            if (cerVectorTaskTb == null) {
                throw new BusinessException("实施方案错误");
            }
            cerVectorTaskTb.setAcceptorMaterial(vectorTaskExcel.getNewBreedName());
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorTaskTb.getTaskNum());
            if (bioTaskDtlTb == null) {
                throw new BusinessException("实施方案发起工单找不到");
            }
            VectorTaskAddDTO vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
            vectorTaskAddDTO.setAcceptorMaterial(vectorTaskExcel.getNewBreedName());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(vectorTaskAddDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

            String[] strArr = vectorTaskExcel.newBreedName.split("\\|");
            for (String breedName : strArr) {
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(breedName, cerVectorTaskTb.getSpeciesCode());
                if (cerBreedDict == null) {
                    cerBreedDict = new CerBreedDict();
                    cerBreedDict.setBreedCode(IdUtils.simpleUUID());
                    cerBreedDict.setBreedName(breedName);
                    cerBreedDict.setSpeciesCode(cerVectorTaskTb.getSpeciesCode());
                    cerBreedDictMapper.insert(cerBreedDict);
                }
            }

        }
        return ResponseResult.getSuccess("ok");

    }


    /**
     * 测试环境清洗
     *
     * @return
     */
    @GetMapping("/cleanVectorTaskBreedStep2")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult cleanVectorTaskBreed() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectList(null);
        for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
            log.info("cerVectorTaskTb={}", JSONUtil.toJsonStr(cerVectorTaskTb));
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(cerVectorTaskTb.getSpeciesCode());
            if (cerSpeciesConf == null) {
                throw new BusinessException("物种不存在");
            }
            List<CerBreedDict> currentCerBreedDictList = cerBreedDictMapper.selectAllBySpeciesCode(cerSpeciesConf.getSpeciesCode());
            if (StringUtils.isEmpty(cerVectorTaskTb.getAcceptorMaterial())) {
                throw new BusinessException("没有受体材料");
            }
            String[] acceptorMaterialNameArr = cerVectorTaskTb.getAcceptorMaterial().split("\\|");
            StringBuffer acceptorMaterialCodeBuf = new StringBuffer("");
            for (String acceptorMaterialName : acceptorMaterialNameArr) {
                if (acceptorMaterialName.length() == 32) {
                    continue;
                }
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(acceptorMaterialName, cerSpeciesConf.getSpeciesCode());
                if (cerBreedDict != null) {
                    acceptorMaterialCodeBuf.append(cerBreedDict.getBreedCode()).append("|");
                } else {
                    throw new BusinessException("找不到品种：" + acceptorMaterialName);
                }
            }
            if (StringUtils.isEmpty(acceptorMaterialCodeBuf)) {
                cerVectorTaskTb.setAcceptorMaterial(currentCerBreedDictList.get(0).getBreedCode());
            } else {
                cerVectorTaskTb.setAcceptorMaterial(acceptorMaterialCodeBuf.substring(0, acceptorMaterialCodeBuf.length() - 1));
            }
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
        }
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 测试清洗
     *
     * @return
     */
    @GetMapping("/cleanVectorTaskBreedStep3")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanVectorTask() {
        List<BioTaskDtlTb> bioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("implementation_plan");
        for (BioTaskDtlTb bioTaskDtlTb : bioTaskDtlTbList) {
            if ("3".equals(bioTaskDtlTb.getTaskStatus()) || "4".equals(bioTaskDtlTb.getTaskStatus())) {
                continue;
            }
            log.info("bioTaskDtlTb={}", bioTaskDtlTb);
            VectorTaskAddDTO vectorTaskAddDTO = null;
            try {
                vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
            } catch (Exception e) {
                throw new BusinessException("实施方案构建数据格式异常");
            }
            String[] acceptorMaterialNameArr = vectorTaskAddDTO.getAcceptorMaterial().split("\\|");
            StringBuffer acceptorMaterialCodeBuf = new StringBuffer("");
            StringBuffer acceptorMaterialNameBuf = new StringBuffer("");

            for (String acceptorMaterialName : acceptorMaterialNameArr) {
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(acceptorMaterialName, vectorTaskAddDTO.getSpeciesCode());
                if (cerBreedDict == null) {
                    log.info("vectorTaskAddDTO.getSpeciesCode() + \"|\" + acceptorMaterialName={},{}", vectorTaskAddDTO.getSpeciesCode(), acceptorMaterialName);
                    throw new BusinessException("找不到品种信息");
                }
                acceptorMaterialNameBuf.append(cerBreedDict.getBreedName()).append("|");
                acceptorMaterialCodeBuf.append(cerBreedDict.getSpeciesCode()).append("|");
            }
            vectorTaskAddDTO.setAcceptorMaterial(acceptorMaterialCodeBuf.substring(0, acceptorMaterialCodeBuf.length() - 1));
            vectorTaskAddDTO.setAcceptorMaterialName(acceptorMaterialNameBuf.substring(0, acceptorMaterialNameBuf.length() - 1));
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(vectorTaskAddDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("cleanTransForm")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult cleanTransForm() {
        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectList(null);
        for (CerTransformTb cerTransformTb : cerTransformTbList) {
            log.info("清洗cerTransformTb={}", JSONUtil.toJsonStr(cerTransformTb));
            if (cerTransformTb.getAcceptorMaterial() == null || cerTransformTb.getAcceptorMaterial().length() == 32) {
                continue;
            }
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerTransformTb.getVectorTaskCode());
            if (cerVectorTaskTb == null) {
                throw new BusinessException("找不到实施方案");
            }


            String acceptorMaterials = cerVectorTaskTb.getAcceptorMaterial();
            String[] acceptorMaterialArr = acceptorMaterials.split("\\|");
            if (acceptorMaterialArr.length == 1) {
                cerTransformTb.setAcceptorMaterial(acceptorMaterialArr[0]);
            } else {
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(cerTransformTb.getAcceptorMaterial(), cerVectorTaskTb.getSpeciesCode());
                if (cerBreedDict != null) {
                    cerTransformTb.setAcceptorMaterial(cerBreedDict.getBreedCode());
                } else {
                    continue;
                }
            }
            cerTransformTbMapper.updateById(cerTransformTb);

            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerTransformTb.getTaskNum());
            TransformDTO transformDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TransformDTO.class);
            if (transformDTO.getContentList() != null) {
                transformDTO.getContentList().forEach(content -> {
                    content.setAcceptorMaterial(cerTransformTb.getAcceptorMaterial());
                    CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(cerTransformTb.getAcceptorMaterial());
                    content.setAcceptorMaterialName(cerBreedDict.getBreedName());

                });
            }

            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(transformDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

        }
        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanSeedStockAddress")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSeedStockAddress() {
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectList(null);
        Map<String, String> seedProduceAddressDictMap = seedProduceAddressDictMapper.selectAll().stream().collect(Collectors.toMap(SeedProduceAddressDict::getAddressName, SeedProduceAddressDict::getAddressCode));
        for (SeedStockTb seedStockTb : seedStockTbList) {
            log.info("清洗seedStockTb={}", JSONUtil.toJsonStr(seedStockTb));
            if (StringUtils.isEmpty(seedStockTb.getProductionLocationCode())) {
                continue;
            }
            if (seedStockTb.getProductionLocationCode().length() == 32) {
                continue;
            }
            if ("未知".equals(seedStockTb.getProductionLocationCode())) {
                seedStockTb.setProductionLocationCode(null);
                seedStockTbMapper.updateById(seedStockTb);
                continue;
            }
            if ("昌平中科院".equals(seedStockTb.getProductionLocationCode())) {
                seedStockTb.setProductionLocationCode("北京昌平中科院");
            } else if ("平西府".equals(seedStockTb.getProductionLocationCode())) {
                seedStockTb.setProductionLocationCode("北京昌平中科院");
            } else if ("山西".equals(seedStockTb.getProductionLocationCode())) {
                seedStockTb.setProductionLocationCode("山西运城");
            } else if ("海南".equals(seedStockTb.getProductionLocationCode())) {
                seedStockTb.setProductionLocationCode("海南中科院");
            } else if ("昌平".equals(seedStockTb.getProductionLocationCode())) {
                seedStockTb.setProductionLocationCode("北京昌平中科院");
            } else if ("武清农场".equals(seedStockTb.getProductionLocationCode())) {
                seedStockTb.setProductionLocationCode("武清大田");
            } else if ("长春".equals(seedStockTb.getProductionLocationCode())) {
                seedStockTb.setProductionLocationCode("武清CER");
            } else if ("武清".equals(seedStockTb.getProductionLocationCode())) {
                //CER
                if ("1".equals(seedStockTb.getSourceType())) {
                    seedStockTb.setProductionLocationCode("武清CER");
                } else if ("2".equals(seedStockTb.getSourceType())) {
                    seedStockTb.setProductionLocationCode("武清玻璃温室");
                } else if ("4".equals(seedStockTb.getSourceType())) {
                    seedStockTb.setProductionLocationCode("武清大田");
                }
            } else if ("CER".equals(seedStockTb.getProductionLocationCode())) {
                seedStockTb.setProductionLocationCode("武清CER");
            }

            SeedProduceAddressDict seedProduceAddressDict = seedProduceAddressDictMapper.selectOneByAddressName(seedStockTb.getProductionLocationCode());
            if (seedProduceAddressDict == null) {
                throw new BusinessException("找不到此生产地点：" + seedStockTb.getProductionLocationCode());
            }
            seedStockTb.setProductionLocationCode(seedProduceAddressDict.getAddressCode());
            seedStockTbMapper.updateById(seedStockTb);

        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanTc")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTc() {
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectList(null);
        for (TcExperimentTb tcExperimentTb : tcExperimentTbList) {
            SeedProduceAddressDict seedProduceAddressDict = seedProduceAddressDictMapper.selectOneByAddressName(tcExperimentTb.getExperimentAddressCode());
            if (seedProduceAddressDict == null) {
                throw new BusinessException("系统不识别此地点，需要先在系统配置");
            }
            tcExperimentTb.setExperimentAddressCode(seedProduceAddressDict.getAddressCode());

            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcExperimentTb.getExperimentNum());
            TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
            tcExperimentTaskDTO.setExperimentAddressCode(seedProduceAddressDict.getAddressCode());
            tcExperimentTaskDTO.setExperimentAddressName(seedProduceAddressDict.getAddressName());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcExperimentTaskDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
            tcExperimentTbMapper.updateById(tcExperimentTb);
        }
        return ResponseResult.getSuccess("ok");
    }


    @Data
    public static class VectorTaskExcel {

        @ExcelProperty("主键")
        private String id;
        @ExcelProperty("实施方案编号")
        private String vectorTaskCode;

        @ExcelProperty("受体材料(旧)")
        private String oldBreedName;

        @ExcelProperty("受体材料(新)")
        private String newBreedName;

        @ExcelProperty("物种")
        private String speciesCode;

    }

}
