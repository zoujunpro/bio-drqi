package com.bio.drqi.manage.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.*;
import com.bio.drqi.mapper.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test")
public class Clean20250721Controller {

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;


    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

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
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;

    @Resource
    private CerVectorStepLogMapper cerVectorStepLogMapper;

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private SeedStockDestructionLogMapper seedStockDestructionLogMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;


    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;


    @Resource
    private BmsProductTbMapper bmsProductTbMapper;


    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;


    @GetMapping("cleanSeedStockNew20251011")
    @Transactional(rollbackFor = Exception.class)
    @WebLog(desc = "cleanSeedStockNew20251011 种子库数据清洗")
    public ResponseResult<String> cleanSeedStockNew20251011() {
        List<Seed> seedList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\种子库数据（各团队更新）.xlsx", Seed.class);
        for (Seed seed : seedList) {
            log.info("清洗当前数据：seed" + JSONUtil.toJsonStr(seed));
            SeedStockTb seedStockTb = seedStockTbMapper.selectById(seed.id);
            if (seedStockTb == null) {
                throw new BusinessException("数据异常，动了excel中ID");
            }
            //数据一致性校验
            if (!seedStockTb.getSeedNum().equals(seed.seedNum)) {
                throw new BusinessException("数据异常，动了excel中种子编号,数据库中种子编号：" + seedStockTb.getSeedNum());
            }
            //代次清洗，如果有代次，校验格式是否正确
            if (StringUtils.isNotEmpty(seed.generation)) {
                String generationNum = GenerationEnum.getGenerationNum(seed.getGeneration());
                if (generationNum == null) {
                    throw new BusinessException("代次填写错误");
                }
                seedStockTb.setGeneration(seed.getGeneration());
            }
            //上代种子编号清洗， 如果有母本种子编号 放入到母本种子编号中
            if (StringUtils.isNotEmpty(seed.parentSeedNum)) {
                SeedStockTb matherSeed = seedStockTbMapper.selectOneBySeedNum(seed.parentSeedNum);
                if (matherSeed == null) {
                    throw new BusinessException("母本种子编号错误");
                }
                seedStockTb.setMatherSeedNum(matherSeed.getSeedNum());
            }
            //父本种植编号清洗
            if(StringUtils.isNotEmpty(seed.fatherSeedNum)){
                SeedStockTb fatherSeed = seedStockTbMapper.selectOneBySeedNum(seed.fatherSeedNum);
                if (fatherSeed == null) {
                    throw new BusinessException("父本种子编号错误");
                }
                seedStockTb.setFatherSeedNum(fatherSeed.getSeedNum());
            }

            //新种植编号清洗，
            if(StringUtils.isNotEmpty(seed.plantNumNew)){
                CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCode(seed.plantNumNew);
                if(cerPlantDtlTb==null){
                    throw new BusinessException("新的种植编号在库存中找不到");
                }
                if(StringUtils.isNotEmpty(seedStockTb.getPlantCode())){
                    seedStockTb.setRemarks(StringUtils.isNotEmpty(seedStockTb.getRemarks()) ? seedStockTb.getRemarks() + "," + seedStockTb.getPlantCode() : seedStockTb.getPlantCode());
                }
                seedStockTb.setPlantCode(seed.plantNumNew);
            }else

            //旧种植编号判断，判断excel中种植编号是否存在，如果是真实存在，则放入种子编号中，原有的种植编号放入到备注
            if (StringUtils.isNotEmpty(seed.getPlantNumOld())) {
                CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCode(seed.plantNumOld);
                if (cerPlantDtlTb != null) {
                    if (StringUtils.isNotEmpty(seedStockTb.getPlantCode())) {
                        seedStockTb.setRemarks(StringUtils.isNotEmpty(seedStockTb.getRemarks()) ? seedStockTb.getRemarks() + "," + seedStockTb.getPlantCode() : seedStockTb.getPlantCode());
                    }
                    seedStockTb.setPlantCode(seed.plantNumOld);
                }else {
                    seedStockTb.setPlantCode(null);
                }
            } else {
                //如果excel中种植编号不存在，校验当前库存中种植编号是否合法,不合法则放入到备注
                if (StringUtils.isNotEmpty(seedStockTb.getPlantCode())) {
                    CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCode(seedStockTb.getPlantCode());
                    if (cerPlantDtlTb == null) {
                        seedStockTb.setRemarks(StringUtils.isNotEmpty(seedStockTb.getRemarks()) ? seedStockTb.getPlantCode() + "," + seedStockTb.getPlantCode() : seedStockTb.getPlantCode());
                        seedStockTb.setPlantCode(null);
                    }
                }

            }
            //根据当前的种植编号反查实施方案号
            if (StringUtils.isNotEmpty(seedStockTb.getPlantCode())) {
                CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCode(seedStockTb.getPlantCode());
                if(cerPlantDtlTb!=null){
                    seedStockTb.setVectorTaskCode(cerPlantDtlTb.getVectorTaskCode());
                    seedStockTb.setProjectCode(cerPlantDtlTb.getProjectCode());
                    if (StringUtils.isNotEmpty(seed.projectCode)) {
                        if (!cerPlantDtlTb.getProjectCode().equals(seed.projectCode)) {
                            log.info(seed.projectCode+":"+cerPlantDtlTb.getProjectCode());
                            throw new BusinessException("项目号和种植编号不匹配");
                        }

                    }
                    if (StringUtils.isNotEmpty(seed.vectorTaskCode)) {
                        if (!cerPlantDtlTb.getVectorTaskCode().equals(seed.vectorTaskCode)) {
                            throw new BusinessException("实施方案号和种植编号不匹配");
                        }
                    }
                }
            } else {

                //项目编号校验和清洗
                if (StringUtils.isNotEmpty(seed.projectCode)) {
                    CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(seed.projectCode);
                    if (cerProjectTb == null) {
                        throw new BusinessException("项目编号非法");
                    }
                    seedStockTb.setProjectCode(seed.projectCode);
                }

                //实施方案号清洗和校验
                if (StringUtils.isNotEmpty(seed.vectorTaskCode)) {
                    CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(seed.vectorTaskCode);
                    if (cerVectorTaskTb == null) {
                        throw new BusinessException("实施方案编号非法");
                    }
                    seedStockTb.setVectorTaskCode(seed.vectorTaskCode);
                }
            }

            seedStockTbMapper.updateById(seedStockTb);

        }

        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectList(null);
        log.info("种植编号清洗");
        for (SeedStockTb seedStockTb : seedStockTbList) {
            if (StringUtils.isNotEmpty(seedStockTb.getPlantCode())) {
                if (cerPlantDtlTbMapper.selectOneByPlantCode(seedStockTb.getPlantCode()) == null) {
                    if (StringUtils.isEmpty(seedStockTb.getRemarks())) {
                        seedStockTbMapper.updatePlantCodeAndRemarksById(null, seedStockTb.getPlantCode(), seedStockTb.getId());
                    } else {
                        if (!seedStockTb.getRemarks().contains(seedStockTb.getPlantCode())) {
                            seedStockTbMapper.updatePlantCodeAndRemarksById(null, seedStockTb.getRemarks() + "," + seedStockTb.getPlantCode(), seedStockTb.getId());
                        }

                    }
                }
            }
        }
        return ResponseResult.getSuccess("ok");

    }

    @Data
    public static class Seed {
        //  主键ID	种子编号	项目编号	世代		上代种子编号 母本种子编号	父本种子编号	种植编号（新）	种植编号（旧，会删除）	实施方案编号
        @ExcelProperty("主键ID")
        private String id;

        @ExcelProperty("种子编号")
        private String seedNum;

        @ExcelProperty("项目编号")
        private String projectCode;

        @ExcelProperty("世代")
        private String generation;

        @ExcelProperty("上代种子编号")
        private String parentSeedNum;

        @ExcelProperty("母本种子编号")
        private String matherSeedNum;

        @ExcelProperty("父本种子编号")
        private String fatherSeedNum;

        @ExcelProperty("种植编号（新）")
        private String plantNumNew;

        @ExcelProperty("种植编号（旧，会删除）")
        private String plantNumOld;

        @ExcelProperty("实施方案编号")
        private String vectorTaskCode;


    }


    @GetMapping("cleanPlasmid")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlasmid() {
        List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectList(null);
        for (CerPlasmidQualityTb cerPlasmidQualityTb : cerPlasmidQualityTbList) {
            log.info("cerPlasmidQualityTb={}", JSONUtil.toJsonStr(cerPlasmidQualityTb));
            if (cerPlasmidQualityTb.getQualityInspectionType().length() == 1) {
                cerPlasmidQualityTb.setQualityInspectionType(JSONUtil.toJsonStr(Arrays.asList(cerPlasmidQualityTb.getQualityInspectionType())));
                cerPlasmidQualityTbMapper.updateById(cerPlasmidQualityTb);
            }
        }
        List<BioTaskDtlTb> list = bioTaskDtlTbMapper.selectAllByTaskTypeCode("plasmid_check");
        for (BioTaskDtlTb bioTaskDtlTb : list) {
            log.info("bioTaskDtlTb={}", JSONUtil.toJsonStr(bioTaskDtlTb));
            PlasmidDTO plasmidDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
            if (CollectionUtil.isEmpty(plasmidDTO.getContentList())) {
                continue;
            }
            for (PlasmidDTO.Content content : plasmidDTO.getContentList()) {
                if (content.getQualityInspectionType().length() == 1) {
                    content.setQualityInspectionType(JSONUtil.toJsonStr(Arrays.asList(content.getQualityInspectionType())));
                }
            }
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plasmidDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("/cleanVector")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanVector() {
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectSelective(null);
        for (CerVectorTb cerVectorTb : cerVectorTbList) {
            log.info("cerVectorTb={}", JSONUtil.toJsonStr(cerVectorTb));
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(cerVectorTb.getVectorTaskId());
            if (cerVectorTaskTb != null) {
                cerVectorTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                cerVectorTbMapper.updateById(cerVectorTb);
            }
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanSubProject")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSubProject() {
        List<CerSubProjectTb> cerSubProjectTbList = cerSubProjectTbMapper.selectSelective(null);
        for (CerSubProjectTb cerSubProjectTb : cerSubProjectTbList) {
            CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(cerSubProjectTb.getProjectId());
            if (cerProjectTb != null) {
                cerSubProjectTb.setProjectCode(cerProjectTb.getProjectCode());
                cerSubProjectTbMapper.updateById(cerSubProjectTb);
            }
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanConversionAndTrans")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanConversionAndTrans() {
        List<CerConversionAndTransRef> list = cerConversionAndTransRefMapper.selectList(null);
        for (CerConversionAndTransRef cerConversionAndTransRef : list) {
            log.info("cerConversionAndTransRef={}", JSONUtil.toJsonStr(cerConversionAndTransRef));
            CerConversionAndTransTb cerConversionAndTransTb = cerConversionAndTransTbMapper.selectById(cerConversionAndTransRef.getConversionAndTransId());
            cerConversionAndTransRef.setTaskNum(cerConversionAndTransTb.getTaskNum());
            cerConversionAndTransRefMapper.updateById(cerConversionAndTransRef);
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanSampleAcceptorMaterial")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSampleAcceptorMaterial() {
        List<CerSampleTestTb> list = cerSampleTestTbMapper.selectList(null);
        for (CerSampleTestTb cerSampleTestTb : list) {
            log.info("cerSampleTestTb={}", JSONUtil.toJsonStr(cerSampleTestTb));
            if (cerSampleTestTb.getAcceptorMaterial() == null || cerSampleTestTb.getAcceptorMaterial().length() == 32) {
                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(cerSampleTestTb.getTransformCode(), cerSampleTestTb.getVectorTaskCode());
                cerSampleTestTb.setAcceptorMaterial(cerTransformTb.getAcceptorMaterial());
                cerSampleTestTbMapper.updateById(cerSampleTestTb);
            }

        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanCloneSampleCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanCloneSampleCode() {
        List<CerSampleTestTb> list = cerSampleTestTbMapper.selectList(null);
        for (CerSampleTestTb cerSampleTestTb : list) {
            log.info("cerSampleTestTb={}", JSONUtil.toJsonStr(cerSampleTestTb));
            if (cerSampleTestTb.getSampleCode().contains("-")) {
                cerSampleTestTb.setCloneSampleCode(cerSampleTestTb.getSampleCode().split("-")[0]);
                cerSampleTestTbMapper.updateById(cerSampleTestTb);
            }

        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanPlantAcceptorMaterial")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlantAcceptorMaterial() {
        List<CerPlantDtlTb> cerPlantDtlTbList = cerPlantDtlTbMapper.selectSelective(null);
        for (CerPlantDtlTb cerPlantDtlTb : cerPlantDtlTbList) {
            log.info("cerPlantDtlTb={}", JSONUtil.toJsonStr(cerPlantDtlTb));
            if (cerPlantDtlTb.getAcceptorMaterial() != null && cerPlantDtlTb.getAcceptorMaterial().length() == 32) {
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllBySampleCode(cerPlantDtlTb.getSampleCode());
                cerPlantDtlTb.setAcceptorMaterial(cerSampleTestTbList.get(0).getAcceptorMaterial());
                cerPlantDtlTbMapper.updateById(cerPlantDtlTb);
            }
        }
        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanStepCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanStepCode() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectSelective(null);
        for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
            log.info("cerVectorTaskTb={}", JSONUtil.toJsonStr(cerVectorTaskTb));
            List<CerVectorStepLog> cerVectorStepLogList = cerVectorStepLogMapper.selectAllByVectorTaskIdOrderById(cerVectorTaskTb.getId());
            if (CollectionUtil.isNotEmpty(cerVectorStepLogList)) {
                cerVectorTaskTb.setCurrentStepCode(cerVectorStepLogList.get(cerVectorStepLogList.size() - 1).getStepCode());
                cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
            }
        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanProjectType")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult cleanProjectType() {
        List<Project> projectList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\project.xlsx", Project.class);
        for (Project project : projectList) {
            log.info("project={}", JSONUtil.toJsonStr(project));
            CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(project.id);
            if ("大田作物".equals(project.type)) {
                cerProjectTb.setProjectCategoryCode("1");
            } else if ("经济作物".equals(project.type)) {
                cerProjectTb.setProjectCategoryCode("2");

            } else if ("合成学作物".equals(project.type)) {
                cerProjectTb.setProjectCategoryCode("3");

            }
            cerProjectTbMapper.updateById(cerProjectTb);

            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerProjectTb.getTaskNum());
            ProjectAddDTO projectAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ProjectAddDTO.class);
            projectAddDTO.setProjectCategoryCode(cerProjectTb.getProjectCategoryCode());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(projectAddDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

        }

        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanBmsStockLocation")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanBmsStockLocation() {
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectList(null);
        for (BmsProductStockTb bmsProductStockTb : bmsProductStockTbList) {
            if (StringUtils.isEmpty(bmsProductStockTb.getStockLocationNumber())) {
                continue;
            }
            if (bmsProductStockTb.getStockLocationNumber().contains("[")) {
                continue;
            }
            bmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(Arrays.asList(bmsProductStockTb.getStockLocationNumber())));
            bmsProductStockTbMapper.updateById(bmsProductStockTb);
        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanVectorTaskDeliveryMethod")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanVectorTaskDeliveryMethod() {
        List<VectorTask> vectorTaskList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\vectorTask.xlsx", VectorTask.class);
        for (VectorTask vectorTask : vectorTaskList) {
            //A 农杆菌转化 B基因枪 P原生质体转化 V病毒载体
            log.info("vectorTask={}", JSONUtil.toJsonStr(vectorTask));
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(vectorTask.id);
            if (vectorTask.getDeliveryMethod().contains("|")) {
                StringBuffer deliveryMethodBuf = new StringBuffer("");
                String[] deliveryMethodArr = vectorTask.getDeliveryMethod().split("\\|");
                for (String str : deliveryMethodArr) {
                    if ("原生质体转化".equals(str)) {
                        deliveryMethodBuf.append("P").append("|");
                    } else if ("农杆菌转化".equals(str)) {
                        deliveryMethodBuf.append("A").append("|");
                    } else if ("基因枪".equals(str)) {
                        deliveryMethodBuf.append("B").append("|");
                    } else if ("病毒载体".equals(str)) {
                        deliveryMethodBuf.append("V").append("|");
                    }
                }
                cerVectorTaskTb.setDeliveryMethod(deliveryMethodBuf.substring(0, deliveryMethodBuf.length() - 1));
            } else {
                if ("原生质体转化".equals(vectorTask.getDeliveryMethod())) {
                    cerVectorTaskTb.setDeliveryMethod("P");
                } else if ("农杆菌转化".equals(vectorTask.deliveryMethod)) {
                    cerVectorTaskTb.setDeliveryMethod("A");
                } else if ("基因枪".equals(vectorTask.deliveryMethod)) {
                    cerVectorTaskTb.setDeliveryMethod("B");
                } else if ("病毒载体".equals(vectorTask.deliveryMethod)) {
                    cerVectorTaskTb.setDeliveryMethod("V");
                }
            }
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorTaskTb.getTaskNum());
            ImplementPlanAddDTO implementPlanAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ImplementPlanAddDTO.class);
            implementPlanAddDTO.setDeliveryMethod(cerVectorTaskTb.getDeliveryMethod());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(implementPlanAddDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }

        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanTransDeliveryMethod")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTransDeliveryMethod() {
        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectSelective(null);
        for (CerTransformTb cerTransformTb : cerTransformTbList) {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerTransformTb.getVectorTaskCode());
            //A 农杆菌转化 B基因枪 P原生质体转化 V病毒载体
            if (cerVectorTaskTb.getDeliveryMethod().contains("|")) {
                if ("原生质体转化".equals(cerTransformTb.getDeliveryMethod())) {
                    cerTransformTb.setDeliveryMethod("P");
                    cerTransformTbMapper.updateById(cerTransformTb);
                } else if ("农杆菌转化".equals(cerTransformTb.getDeliveryMethod())) {
                    cerTransformTb.setDeliveryMethod("A");
                    cerTransformTbMapper.updateById(cerTransformTb);
                } else if ("基因枪".equals(cerTransformTb.getDeliveryMethod())) {
                    cerTransformTb.setDeliveryMethod("B");
                    cerTransformTbMapper.updateById(cerTransformTb);
                } else if ("病毒载体".equals(cerTransformTb.getDeliveryMethod())) {
                    cerTransformTb.setDeliveryMethod("V");
                    cerTransformTbMapper.updateById(cerTransformTb);
                }
            } else {
                cerTransformTb.setDeliveryMethod(cerVectorTaskTb.getDeliveryMethod());
                cerTransformTbMapper.updateById(cerTransformTb);
            }
        }

        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanTransTask")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTransTask() {
        List<BioTaskDtlTb> bioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("transform");
        for (BioTaskDtlTb bioTaskDtlTb : bioTaskDtlTbList) {
            TransformDTO transformDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TransformDTO.class);
            if (CollectionUtil.isNotEmpty(transformDTO.getContentList())) {
                for (TransformDTO.Content content : transformDTO.getContentList()) {
                    CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(content.getTransformCode(), transformDTO.getVectorTaskCode());
                    if (cerTransformTb != null) {
                        content.setDeliveryMethod(cerTransformTb.getDeliveryMethod());
                    }
                }
            }
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(transformDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

        }
        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanVectorTaskOther")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanVectorTaskOther() {
        List<VectorTask> vectorTaskList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\实施方案.xlsx", VectorTask.class);
        for (VectorTask vectorTask : vectorTaskList) {
            log.info("vectorTask={}", JSONUtil.toJsonStr(vectorTask));
            //1 KO，2点突变，3精准小，4精准大 ,5随机转基因
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(vectorTask.id);
            if ("KO".equals(vectorTask.edit_type)) {
                cerVectorTaskTb.setEditType("1");
            } else if ("点突变".equals(vectorTask.edit_type)) {
                cerVectorTaskTb.setEditType("2");
            } else if ("精准小".equals(vectorTask.edit_type)) {
                cerVectorTaskTb.setEditType("3");
            } else if ("精准大".equals(vectorTask.edit_type)) {
                cerVectorTaskTb.setEditType("4");
            } else if ("随机转基因".equals(vectorTask.edit_type)) {
                cerVectorTaskTb.setEditType("5");
            } else {
                throw new BusinessException("不识别编辑类型");
            }

            //1 无，2 ； 3 transgene-free
            if ("transgene-free".equals(vectorTask.supervision_level_code)) {
                cerVectorTaskTb.setSupervisionLevelCode("3");
            } else if ("DNA-free".equals(vectorTask.supervision_level_code)) {
                cerVectorTaskTb.setSupervisionLevelCode("2");
            } else {
                cerVectorTaskTb.setSupervisionLevelCode("1");
            }
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorTaskTb.getTaskNum());
            ImplementPlanAddDTO implementPlanAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ImplementPlanAddDTO.class);
            implementPlanAddDTO.setEditType(cerVectorTaskTb.getEditType());
            implementPlanAddDTO.setSupervisionLevelCode(cerVectorTaskTb.getSupervisionLevelCode());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(implementPlanAddDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanBmsData")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanBmsData() {
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectSelective(null);
        for (BmsOrderDetailTb bmsOrderDetailTb : bmsOrderDetailTbList) {
            if (StringUtils.isEmpty(bmsOrderDetailTb.getBrandCode())) {
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsOrderDetailTb.getProductInnerCode());
                bmsOrderDetailTb.setBrandCode(bmsProductTb.getBrandCode());
                bmsOrderDetailTb.setBrandName(bmsProductTb.getBrandName());
                bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
            }
            if (StringUtils.isEmpty(bmsOrderDetailTb.getProductCategoryCode())) {
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsOrderDetailTb.getProductInnerCode());
                BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryCode(bmsProductTb.getProductCategoryCode());
                bmsOrderDetailTb.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
                bmsOrderDetailTb.setProductCategoryName(bmsProductCategoryTb.getProductCategoryName());
                bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
            }
        }

        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(null);
        for (BmsProductStockTb bmsProductStockTb : bmsProductStockTbList) {
            if (StringUtils.isEmpty(bmsProductStockTb.getBrandCode())) {
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsProductStockTb.getProductInnerCode());
                bmsProductStockTb.setBrandCode(bmsProductTb.getBrandCode());
                bmsProductStockTb.setBrandName(bmsProductTb.getBrandName());
                bmsProductStockTbMapper.updateById(bmsProductStockTb);
            }
            if (StringUtils.isEmpty(bmsProductStockTb.getProductCategoryCode())) {
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsProductStockTb.getProductInnerCode());
                bmsProductStockTb.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
                bmsProductStockTbMapper.updateById(bmsProductStockTb);
            }
        }

        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectList(null);
        for (BmsProductStockInLog bmsProductStockInLog : bmsProductStockInLogList) {
            if (StringUtils.isEmpty(bmsProductStockInLog.getBrandCode())) {
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsProductStockInLog.getProductInnerCode());
                bmsProductStockInLog.setBrandCode(bmsProductTb.getBrandCode());
                bmsProductStockInLog.setBrandName(bmsProductTb.getBrandName());
                bmsProductStockInLogMapper.updateById(bmsProductStockInLog);
            }
            if (StringUtils.isEmpty(bmsProductStockInLog.getProductCategoryCode())) {
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsProductStockInLog.getProductInnerCode());
                bmsProductStockInLog.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
                bmsProductStockInLogMapper.updateById(bmsProductStockInLog);
            }
        }


        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectSelective(null);
        for (BmsProductStockOutLog bmsProductStockOutLog : bmsProductStockOutLogList) {
            if (StringUtils.isEmpty(bmsProductStockOutLog.getBrandCode())) {
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsProductStockOutLog.getProductInnerCode());
                bmsProductStockOutLog.setBrandCode(bmsProductTb.getBrandCode());
                bmsProductStockOutLog.setBrandName(bmsProductTb.getBrandName());
                bmsProductStockOutLogMapper.updateById(bmsProductStockOutLog);
            }
            if (StringUtils.isEmpty(bmsProductStockOutLog.getProductCategoryCode())) {
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsProductStockOutLog.getProductInnerCode());
                bmsProductStockOutLog.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
                bmsProductStockOutLogMapper.updateById(bmsProductStockOutLog);
            }
        }

        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/createBmsStockExcel")
    public void createBmsStockExcel(HttpServletResponse httpServletResponse) {
        Date pointDate = DateUtil.parse("20250701000000", DatePattern.PURE_DATETIME_PATTERN);
        //step 数据查询
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(null);
        Map<String, BmsProductStockTb> bmsProductStockTbMap = bmsProductStockTbList.stream().collect(Collectors.toMap(bmsProductStockTb -> bmsProductStockTb.getProductInnerCode() + bmsProductStockTb.getUnitCode() + bmsProductStockTb.getBatchNo() + bmsProductStockTb.getStockCode(), bmsProductStockTb -> bmsProductStockTb));


        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectList(null);
        System.out.println("bmsProductStockInLogList :" + bmsProductStockInLogList.size());

        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectSelective(null);
        System.out.println("bmsProductStockOutLogList :" + bmsProductStockOutLogList.size());


        List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectList(null);
        System.out.println("bmsMoveOrderDetailTbList :" + bmsMoveOrderDetailTbList.size());


        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectList(null);
        System.out.println("bmsReturnOrderDetailTbList :" + bmsReturnOrderDetailTbList.size());

        //时间过滤
        bmsProductStockInLogList = bmsProductStockInLogList.stream().filter(bmsProductStockInLog -> bmsProductStockInLog.getCreateTime().compareTo(pointDate) > 0).collect(Collectors.toList());
        System.out.println("bmsProductStockInLogList filter:" + bmsProductStockInLogList.size());

        bmsProductStockOutLogList = bmsProductStockOutLogList.stream().filter(bmsProductStockOutLog -> bmsProductStockOutLog.getCreateTime().compareTo(pointDate) > 0).collect(Collectors.toList());
        System.out.println("bmsProductStockOutLogList filter:" + bmsProductStockOutLogList.size());

        bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbList.stream().filter(bmsMoveOrderDetailTb -> bmsMoveOrderDetailTb.getCreateTime().compareTo(pointDate) > 0).collect(Collectors.toList());
        System.out.println("bmsMoveOrderDetailTbList filter:" + bmsMoveOrderDetailTbList.size());

        bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbList.stream().filter(bmsReturnOrderDetailTb -> bmsReturnOrderDetailTb.getCreateTime().compareTo(pointDate) > 0).collect(Collectors.toList());
        System.out.println("bmsReturnOrderDetailTbList filter:" + bmsReturnOrderDetailTbList.size());
        //复原库存
        //先复原出库  出库的数据加到库存中
        for (BmsProductStockOutLog bmsProductStockOutLog : bmsProductStockOutLogList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsProductStockOutLog.getProductInnerCode() + bmsProductStockOutLog.getUnitCode() + bmsProductStockOutLog.getBatchNo() + bmsProductStockOutLog.getStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockOutLog.getOutNumber() + bmsProductStockTb.getCurrentStockNumber());
        }
        //复原退货
        for (BmsReturnOrderDetailTb bmsReturnOrderDetailTb : bmsReturnOrderDetailTbList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsReturnOrderDetailTb.getProductInnerCode() + bmsReturnOrderDetailTb.getUnitCode() + bmsReturnOrderDetailTb.getBatchNo() + bmsReturnOrderDetailTb.getStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsReturnOrderDetailTb.getReturnNumber() + bmsProductStockTb.getCurrentStockNumber());
        }
        //复原调拨
        for (BmsMoveOrderDetailTb bmsMoveOrderDetailTb : bmsMoveOrderDetailTbList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsMoveOrderDetailTb.getProductInnerCode() + bmsMoveOrderDetailTb.getUnitCode() + bmsMoveOrderDetailTb.getBatchNo() + bmsMoveOrderDetailTb.getFromStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsMoveOrderDetailTb.getMoveNumber() + bmsProductStockTb.getCurrentStockNumber());
        }
        //回退入库的
        for (BmsProductStockInLog bmsProductStockInLog : bmsProductStockInLogList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsProductStockInLog.getProductInnerCode() + bmsProductStockInLog.getUnitCode() + bmsProductStockInLog.getBatchNo() + bmsProductStockInLog.getStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber() - bmsProductStockInLog.getStoreNumber());
        }
        //回退调拨的
        for (BmsMoveOrderDetailTb bmsMoveOrderDetailTb : bmsMoveOrderDetailTbList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsMoveOrderDetailTb.getProductInnerCode() + bmsMoveOrderDetailTb.getUnitCode() + bmsMoveOrderDetailTb.getBatchNo() + bmsMoveOrderDetailTb.getToStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber() - bmsMoveOrderDetailTb.getMoveNumber());
        }
        List<BmsStock> bmsStockList = BeanUtils.copyListProperties(bmsProductStockTbList, BmsStock.class);

        bmsStockList = bmsStockList.stream().filter(bmsStock -> bmsStock.getCurrentStockNumber() > 0).collect(Collectors.toList());
        for (BmsStock bmsStock : bmsStockList) {
            List<BmsProductStockInLog> bmsProductStockInLogs = bmsProductStockInLogMapper.selectAllByUniqueCode(bmsStock.getUniqueCode());
            if (CollectionUtil.isNotEmpty(bmsProductStockInLogs)) {
                String projectCode = bmsProductStockInLogs.get(0).getProjectCode();
                BmsProjectDict bmsProjectDict = bmsProjectDictMapper.selectOneByProjectCode(projectCode);
                bmsStock.setProjectCode(bmsProjectDict.getProjectCode());
                bmsStock.setProjectType(bmsProjectDict.getKdProjectType());
                bmsStock.setProductName(bmsProjectDict.getKdProjectName());
            }
        }

        ExcelUtil.writeExcel("D://7月1号之后数据.xlsx", "sheet1", bmsStockList, BmsStock.class);
    }


    @GetMapping("/cleanSeedStock")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSeedStock() {
        List<Species> speciesList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\物种库 - 副本.xlsx", Species.class);
        List<SpeciesNew> speciesNewList = BeanUtils.copyListProperties(speciesList, SpeciesNew.class);
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        Map<String, CerSpeciesConf> cerSpeciesConfMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, cerSpeciesConf -> cerSpeciesConf));
        for (SpeciesNew speciesNew : speciesNewList) {
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMap.get(speciesNew.code);
            if (cerSpeciesConf == null) {
                throw new BusinessException("找不到物种：" + speciesNew.name);
            }
            speciesNew.setOldPrefix(cerSpeciesConf.getNumPrefix());
        }
        ExcelUtil.writeExcel("C:\\Users\\zou'jun\\Desktop\\上线\\物种库(新).xlsx", "sheet1", speciesList, Species.class);
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("testTransForm")
    public String testTransForm() {
        String vectorTaskCode = "EB00701-02b";
        String deliveryMethod = "A";
        String infectDate = "2025-07-01";
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllBySpeciesCodeAndDeliveryMethodAndCreateTime(cerVectorTaskTb.getSpeciesCode(), deliveryMethod, infectDate);
        cerTransformTbList = cerTransformTbList.stream().filter(cerTransformTb -> cerTransformTb.getTransformCode().matches("^[A-Z]{3}[0-9]{6}$")).collect(Collectors.toList());
        String nextNumber = null;
        if (CollectionUtil.isEmpty(cerTransformTbList)) {
            nextNumber = "01";
        } else {
            nextNumber = StringUtils.padl(String.valueOf(Integer.parseInt(cerTransformTbList.get(0).getTransformCode().substring(7)) + 1), 2, '0');
        }
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(cerVectorTaskTb.getSpeciesCode());
        return cerSpeciesConf.getNumPrefix().substring(2) + cerVectorTaskTb.getDeliveryMethod() + infectDate.replace("-", "").substring(4) + nextNumber;
    }


    @Data
    public static class Species {

        @ExcelProperty("品种名称")
        private String name;

        @ExcelProperty("品种编号")
        private String code;

        @ExcelProperty("种子编号前缀")
        private String newPrefix;

    }

    @Data
    public static class SpeciesNew {

        @ExcelProperty("品种名称")
        private String name;

        @ExcelProperty("品种编号")
        private String code;

        @ExcelProperty("种子编号前缀(新)")
        private String newPrefix;


        @ExcelProperty("种子编号前缀(旧)")
        private String oldPrefix;
    }

    @Data
    public static class BmsStock {
        /**
         * 主键ID
         */
        @ExcelProperty("主键ID")
        private Integer id;

        /**
         * 商品名称
         */
        @ExcelProperty("商品名称")
        private String productName;


        /**
         * 所属类别编号
         */
        @ExcelProperty("所属类别编号")
        private String productCategoryCode;


        /**
         * 品牌编号
         */
        @ExcelProperty("品牌编号")
        private String brandCode;

        /**
         * 品牌名称
         */
        @ExcelProperty("品牌名称")
        private String brandName;

        /**
         * 商品规格
         */
        @ExcelProperty("商品规格")
        private String productSpecs;

        /**
         * 商品批次
         */
        @ExcelProperty("商品批次")
        private String batchNo;

        /**
         * 当前库存数量
         */
        @ExcelProperty("当前库存数量")
        private Integer currentStockNumber;

        /**
         * 单位
         */
        @ExcelProperty("单位")
        private String unitCode;


        @ExcelProperty("商品编号")
        private String productInnerCode;

        @ExcelProperty("供应商编号")
        private String supplierCode;

        @ExcelProperty("供应商名称")
        private String supplierName;


        @ExcelProperty("生产日期")
        private String produceDate;


        @ExcelProperty("库房编号")
        private String stockCode;

        @ExcelProperty("项目编号")
        private String projectCode;

        @ExcelProperty("项目名称")
        private String projectName;

        @ExcelProperty("项目类型")
        private String projectType;
        /**
         * 唯一编号
         */
        private String uniqueCode;
    }

    @Data
    public static class VectorTask {


        @ExcelProperty("id")
        private String id;


        @ExcelProperty("实施方案编号")
        private String code;

        @ExcelProperty("递送方式")
        private String deliveryMethod;

        @ExcelProperty("监管级别")
        private String supervision_level_code;

        @ExcelProperty("编辑类型")
        private String edit_type;
    }

    @Data
    public static class Project {

        @ExcelProperty("id")
        private Integer id;

        @ExcelProperty("项目名称")
        private String name;

        @ExcelProperty("项目编号")
        private String code;

        @ExcelProperty("项目分类")
        private String type;
    }


}
