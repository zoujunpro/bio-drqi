package com.bio.drqi.manage.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.SeedMaterialTypeEnum;
import com.bio.drqi.common.enums.SeedSourceEnum;
import com.bio.drqi.manage.devOps.DevOpsModifyProjectCodeReqDTO;
import com.bio.drqi.manage.devOps.DevOpsModifySubProjectCodeReqDTO;
import com.bio.drqi.manage.devOps.DevOpsModifyVectorTaskCodeBreedCodeReqDTO;
import com.bio.drqi.manage.devOps.DevOpsModifyVectorTaskCodeReqDTO;
import com.bio.drqi.manage.dto.plant.task.PlantExperimentTaskDTO;
import com.bio.drqi.manage.dto.project.*;
import com.bio.drqi.manage.dto.seed.SeedStockDevOpsExcelDTO;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DevOpsServiceImpl implements DevOpsService {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;


    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private BioSampleCodePrefixTbMapper bioSampleCodePrefixTbMapper;

    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;

    @Resource
    private BioSampleApplyTbMapper bioSampleApplyTbMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private SeedStockOutLogMapper seedStockOutLogMapper;

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Resource
    private PlantApplyTbMapper plantApplyTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private BioDictMapper bioDictMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;


    public void modifyVectorTaskCodeBreedCode(@RequestBody DevOpsModifyVectorTaskCodeBreedCodeReqDTO devOpsModifyVectorTaskCodeBreedCodeReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(devOpsModifyVectorTaskCodeBreedCodeReqDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("无此实施方案号");
        }
        CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(devOpsModifyVectorTaskCodeBreedCodeReqDTO.getBreedName(), cerVectorTaskTb.getSpeciesCode());
        if (cerBreedDict == null) {
            throw new BusinessException("物种" + cerVectorTaskTb.getBreedCode() + "下无此品种信息");
        }
        cerVectorTaskTb.setBreedCode(cerBreedDict.getBreedCode());
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        if (CollectionUtil.isNotEmpty(cerTransformTbList)) {
            for (CerTransformTb cerTransformTb : cerTransformTbList) {
                cerTransformTb.setBreedCode(cerBreedDict.getBreedCode());
                cerTransformTbMapper.updateById(cerTransformTb);

                PlantMultipleStockTb plantMultipleStockTb = plantMultipleStockTbMapper.selectOneByVectorTaskCodeAndTransformCode(cerTransformTb.getVectorTaskCode(), cerTransformTb.getTransformCode());
                if (plantMultipleStockTb != null) {
                    plantMultipleStockTb.setBreedCode(cerBreedDict.getBreedCode());
                    plantMultipleStockTbMapper.updateById(plantMultipleStockTb);
                }
            }
        }

        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByVectorTaskCodeAndSourceCode(cerVectorTaskTb.getVectorTaskCode(), SourceCodeEnum.project.name());
        if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
            for (BioSampleTestTb bioSampleTestTb : bioSampleTestTbList) {
                bioSampleTestTb.setBreedCode(cerBreedDict.getBreedCode());
                bioSampleTestTbMapper.updateById(bioSampleTestTb);
            }
            List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectAllByPlantCodeIn(bioSampleTestTbList.stream().map(BioSampleTestTb::getSampleCode).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(plantSingleStockTbList)) {
                for (PlantSingleStockTb plantSingleStockTb : plantSingleStockTbList) {
                    plantSingleStockTb.setBreedCode(cerBreedDict.getBreedCode());
                    plantSingleStockTbMapper.updateById(plantSingleStockTb);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanSubProjectCode(DevOpsModifySubProjectCodeReqDTO devOpsModifySubProjectCodeReqDTO) {
        log.info("子项目编号修改开始");
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(devOpsModifySubProjectCodeReqDTO.getOldSubProjectCode());
        if (cerSubProjectTb == null) {
            throw new BusinessException("找不到子项目编号");
        }
        cerSubProjectTb.setSubProjectCode(devOpsModifySubProjectCodeReqDTO.getNewSubProjectCode());
        cerSubProjectTbMapper.updateById(cerSubProjectTb);

        BioTaskDtlTb subProjectBioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerSubProjectTb.getTaskNum());
        SubProjectCreateDTO subProjectCreateDTO = JSONUtil.toBean(subProjectBioTaskDtlTb.getTaskForm(), SubProjectCreateDTO.class);
        if (CollectionUtil.isNotEmpty(subProjectCreateDTO.getContentList())) {
            subProjectCreateDTO.getContentList().forEach(content -> {
                content.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
            });
            subProjectBioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(subProjectBioTaskDtlTb));
            bioTaskDtlTbMapper.updateById(subProjectBioTaskDtlTb);
        }
        //修改实施方案编号和所属子项目编号
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllBySubProjectId(cerSubProjectTb.getId());
        if (CollectionUtil.isNotEmpty(cerVectorTaskTbList)) {
            for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
                cleanVectorTaskCode(cerVectorTaskTb.getVectorTaskCode(), cerSubProjectTb.getSubProjectCode() + cerVectorTaskTb.getVectorTaskCode().substring(cerVectorTaskTb.getVectorTaskCode().lastIndexOf("-")), cerSubProjectTb.getProjectCode(), cerSubProjectTb.getSubProjectCode());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanVectorTaskCode(DevOpsModifyVectorTaskCodeReqDTO devOpsModifySubProjectCodeReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(devOpsModifySubProjectCodeReqDTO.getOldVectorTaskCode());
        cleanVectorTaskCode(cerVectorTaskTb.getVectorTaskCode(), cerVectorTaskTb.getSubProjectCode() + cerVectorTaskTb.getVectorTaskCode().substring(cerVectorTaskTb.getVectorTaskCode().lastIndexOf("-")), cerVectorTaskTb.getProjectCode(), cerVectorTaskTb.getSubProjectCode());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanProjectCode(DevOpsModifyProjectCodeReqDTO devOpsModifyProjectCodeReqDTO) {
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(devOpsModifyProjectCodeReqDTO.getOldProjectCode());
        cerProjectTb.setProjectCode(devOpsModifyProjectCodeReqDTO.getNewProjectCode());
        cerProjectTbMapper.updateById(cerProjectTb);

        BioTaskDtlTb projectBioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerProjectTb.getTaskNum());
        ProjectAddDTO projectAddDTO = JSONUtil.toBean(projectBioTaskDtlTb.getTaskForm(), ProjectAddDTO.class);
        projectAddDTO.setProjectCode(devOpsModifyProjectCodeReqDTO.getNewProjectCode());
        projectBioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(projectAddDTO));
        bioTaskDtlTbMapper.updateById(projectBioTaskDtlTb);

        List<CerSubProjectTb> cerSubProjectTbList = cerSubProjectTbMapper.selectAllByProjectId(cerProjectTb.getId());
        for (CerSubProjectTb cerSubProjectTb : cerSubProjectTbList) {
            cerSubProjectTb.setProjectCode(devOpsModifyProjectCodeReqDTO.getNewProjectCode());
            cerSubProjectTb.setSubProjectCode(devOpsModifyProjectCodeReqDTO.getNewProjectCode() + cerSubProjectTb.getSubProjectCode().substring(devOpsModifyProjectCodeReqDTO.getOldProjectCode().length()));
            cerSubProjectTbMapper.updateById(cerSubProjectTb);

            BioTaskDtlTb subProjectBioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerSubProjectTb.getTaskNum());
            SubProjectCreateDTO subProjectCreateDTO = JSONUtil.toBean(subProjectBioTaskDtlTb.getTaskForm(), SubProjectCreateDTO.class);
            subProjectCreateDTO.setProjectCode(devOpsModifyProjectCodeReqDTO.getNewProjectCode());
            if (CollectionUtil.isNotEmpty(subProjectCreateDTO.getContentList())) {
                subProjectCreateDTO.getContentList().forEach(content -> {
                    content.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
                });
            }
            subProjectBioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(subProjectBioTaskDtlTb));
            bioTaskDtlTbMapper.updateById(subProjectBioTaskDtlTb);

            List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllBySubProjectId(cerSubProjectTb.getId());
            for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
                cleanVectorTaskCode(cerVectorTaskTb.getVectorTaskCode(), cerSubProjectTb.getSubProjectCode() + cerVectorTaskTb.getVectorTaskCode().substring(cerVectorTaskTb.getVectorTaskCode().lastIndexOf("-")), cerSubProjectTb.getProjectCode(), cerSubProjectTb.getSubProjectCode());
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProjectCode(String projectCode) {
        //删除项目信息
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(projectCode);
        if (cerProjectTb == null) {
            throw new BusinessException("找不到项目信息");
        }
        cerProjectTbMapper.deleteById(cerProjectTb.getId());
        bioTaskDtlTbMapper.deleteByTaskNum(cerProjectTb.getTaskNum());

        //删除子项目信息
        List<CerSubProjectTb> cerSubProjectTbList = cerSubProjectTbMapper.selectAllByProjectId(cerProjectTb.getId());
        if (CollectionUtil.isNotEmpty(cerSubProjectTbList)) {
            cerSubProjectTbList.forEach(cerSubProjectTb -> {
                cerSubProjectTbMapper.deleteById(cerSubProjectTb.getId());
                bioTaskDtlTbMapper.deleteByTaskNum(cerSubProjectTb.getTaskNum());

                List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllBySubProjectId(cerSubProjectTb.getId());
                for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
                    deleteByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                }
            });
        }
    }

    @Override
    public void deleteBySubProjectCode(String subProjectCode) {
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(subProjectCode);
        if (cerSubProjectTb == null) {
            throw new BusinessException("找不到子项目信息");
        }
        bioTaskDtlTbMapper.deleteByTaskNum(cerSubProjectTb.getTaskNum());

        cerSubProjectTbMapper.deleteById(cerSubProjectTb.getId());

        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllBySubProjectId(cerSubProjectTb.getId());

        for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
            deleteByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByVectorTaskCode(String vectorTaskCode) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        cerVectorTaskTbMapper.deleteById(cerVectorTaskTb.getId());
        bioTaskDtlTbMapper.deleteByTaskNum(cerVectorTaskTb.getTaskNum());

        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        if (CollectionUtil.isNotEmpty(cerVectorTbList)) {
            cerVectorTbList.forEach(cerVectorTb -> {
                cerVectorTbMapper.deleteById(cerVectorTb.getId());
                bioTaskDtlTbMapper.deleteByTaskNum(cerVectorTb.getTaskNum());
            });
        }
        List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        if (CollectionUtil.isNotEmpty(cerPlasmidQualityTbList)) {
            cerPlasmidQualityTbList.forEach(cerPlasmidQualityTb -> {
                cerPlasmidQualityTbMapper.deleteById(cerPlasmidQualityTb.getId());
                bioTaskDtlTbMapper.deleteByTaskNum(cerPlasmidQualityTb.getTaskNum());
            });
        }

        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        if (CollectionUtil.isNotEmpty(cerTransformTbList)) {
            cerTransformTbList.forEach(cerTransformTb -> {
                cerTransformTbMapper.deleteById(cerTransformTb);
                bioTaskDtlTbMapper.deleteByTaskNum(cerTransformTb.getTaskNum());
            });
        }

        List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectAllByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        if (CollectionUtil.isNotEmpty(cerConversionAndTransRefList)) {
            cerConversionAndTransRefList.forEach(cerConversionAndTransRef -> {
                cerConversionAndTransRefMapper.deleteById(cerConversionAndTransRef.getId());
                cerConversionAndTransTbMapper.deleteByTaskNum(cerConversionAndTransRef.getTaskNum());
                bioTaskDtlTbMapper.deleteByTaskNum(cerConversionAndTransRef.getTaskNum());
            });
        }

        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
            bioSampleTestTbList.forEach(bioSampleTestTb -> {
                bioSampleTestTbMapper.deleteById(bioSampleTestTb.getId());
            });
            List<String> applyNoList = bioSampleTestTbList.stream().map(BioSampleTestTb::getApplyNo).distinct().collect(Collectors.toList());
            for (String applyNo : applyNoList) {
                if (CollectionUtil.isEmpty(bioSampleTestTbMapper.selectAllByApplyNo(applyNo))) {
                    bioSampleApplyTbMapper.deleteByApplyNo(applyNo);
                    bioTaskDtlTbMapper.deleteByTaskNum(applyNo);
                }
            }
        }
        List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectAllByVectorTaskCode(vectorTaskCode);
        if (CollectionUtil.isNotEmpty(plantSingleStockTbList)) {
            plantSingleStockTbList.forEach(plantSingleStockTb -> {
                plantSingleStockTbMapper.deleteById(plantSingleStockTb.getId());
            });
        }
        List<PlantMultipleStockTb> plantMultipleStockTbList = plantMultipleStockTbMapper.selectAllByVectorTaskCode(vectorTaskCode);
        if (CollectionUtil.isNotEmpty(plantMultipleStockTbList)) {
            plantMultipleStockTbList.forEach(plantMultipleStockTb -> {
                plantMultipleStockTbMapper.deleteById(plantMultipleStockTb.getId());
            });
        }
        BioSampleCodePrefixTb bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (bioSampleCodePrefixTb != null) {
            bioSampleCodePrefixTbMapper.deleteById(bioSampleCodePrefixTb);
        }

    }

    @Override
    public void exportSeedStock(HttpServletResponse httpServletResponse) {
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectAllForexportSeedStock();
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        List<SeedProduceAddressDict> seedProduceAddressDictList = seedProduceAddressDictMapper.selectAll();
        Map<String, String> seedProduceAddressDictMap = seedProduceAddressDictList.stream().collect(Collectors.toMap(SeedProduceAddressDict::getAddressCode, SeedProduceAddressDict::getAddressName));
        Map<String, String> speciesMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        Map<String, String> cerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        List<BioDict> bioDictList = bioDictMapper.selectAll();
        Map<String, BioDict> bioDictMap = bioDictList.stream().collect(Collectors.toMap(bioDict -> bioDict.getDictType() + ":" + bioDict.getDictValueCode(), bioDict -> bioDict));
        List<SeedStockDevOpsExcelDTO> seedStockDevOpsExcelDTOList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(seedStockTbList)) {
            seedStockDevOpsExcelDTOList = BeanUtils.copyToList(seedStockTbList, SeedStockDevOpsExcelDTO.class);
            for (SeedStockDevOpsExcelDTO seedStockDevOpsExcelDTO : seedStockDevOpsExcelDTOList) {
                seedStockDevOpsExcelDTO.setSpeciesName(speciesMap.get(seedStockDevOpsExcelDTO.getSpeciesCode()));
                seedStockDevOpsExcelDTO.setBreedName(cerBreedDictMap.get(seedStockDevOpsExcelDTO.getBreedCode()));
                seedStockDevOpsExcelDTO.setProductionLocationCodeName(seedProduceAddressDictMap.get(seedStockDevOpsExcelDTO.getProductionLocationCode()));
                seedStockDevOpsExcelDTO.setSourceTypeName(SeedSourceEnum.getByCode(seedStockDevOpsExcelDTO.getSourceType()).name);
                seedStockDevOpsExcelDTO.setMaterialType(SeedMaterialTypeEnum.getSeedMaterialTypeEnumByType(seedStockDevOpsExcelDTO.getMaterialType()).name);
                List<SeedStockOutLog> seedStockOutLogList = seedStockOutLogMapper.selectAllBySeedNum(seedStockDevOpsExcelDTO.getSeedNum());
                if (CollectionUtil.isNotEmpty(seedStockOutLogList)) {
                    seedStockDevOpsExcelDTO.setOutTaskNum(JSONUtil.toJsonStr(seedStockOutLogList.stream().map(SeedStockOutLog::getTaskNum).collect(Collectors.toList())));
                }
                if (StringUtils.isNotEmpty(seedStockDevOpsExcelDTO.getPollinationMethod())) {
                    BioDict pollinationMethodBioDict = bioDictMap.get(BioDictTypeEnum.POLLINATE_TYPE + ":" + seedStockDevOpsExcelDTO.getPollinationMethod());
                    if (pollinationMethodBioDict != null) {
                        seedStockDevOpsExcelDTO.setPollinationMethodName(pollinationMethodBioDict.getDictValueName());
                    }
                }
                if (StringUtils.isNotEmpty(seedStockDevOpsExcelDTO.getHarvestType())) {
                    BioDict pollinationMethodBioDict = bioDictMap.get(BioDictTypeEnum.HARVEST_TYPE + ":" + seedStockDevOpsExcelDTO.getHarvestType());
                    if (pollinationMethodBioDict != null) {
                        seedStockDevOpsExcelDTO.setHarvestTypeName(pollinationMethodBioDict.getDictValueName());
                    }


                }
            }
        }
        ExcelUtil.writeExcel("种子库", "sheet1", seedStockDevOpsExcelDTOList, SeedStockDevOpsExcelDTO.class, httpServletResponse);

    }

    private void cleanVectorTaskCode(String oldVectorTaskCode, String newVectorTaskCode, String projectCode, String subProjectCode) {
        //修改实施方案编号和所属子项目编号
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(oldVectorTaskCode);

        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(subProjectCode);

        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(projectCode);

        log.info("修改实施方案编号");
        cerVectorTaskTb.setVectorTaskCode(newVectorTaskCode);
        cerVectorTaskTb.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        cerVectorTaskTb.setProjectCode(cerProjectTb.getProjectCode());
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

        BioTaskDtlTb vectorTaskCodeBioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorTaskTb.getTaskNum());
        ImplementPlanAddDTO implementPlanAddDTO = JSONUtil.toBean(vectorTaskCodeBioTaskDtlTb.getTaskForm(), ImplementPlanAddDTO.class);
        implementPlanAddDTO.setProjectCode(projectCode);
        implementPlanAddDTO.setVectorTaskCode(newVectorTaskCode);
        implementPlanAddDTO.setSubProjectCode(subProjectCode);
        vectorTaskCodeBioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(implementPlanAddDTO));
        bioTaskDtlTbMapper.updateById(vectorTaskCodeBioTaskDtlTb);

        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        if (CollectionUtil.isNotEmpty(cerVectorTbList)) {
            cerVectorTbList.forEach(cerVectorTb -> {
                log.info("修改载体构建信息");
                cerVectorTb.setVectorTaskCode(newVectorTaskCode);
                cerVectorTbMapper.updateById(cerVectorTb);

                BioTaskDtlTb vectorBioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorTb.getTaskNum());
                VectorTaskAddDTO vectorTaskAddDTO = JSONUtil.toBean(vectorBioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
                vectorTaskAddDTO.setProjectCode(projectCode);
                vectorTaskAddDTO.setSubProjectCode(subProjectCode);
                vectorTaskAddDTO.setVectorTaskCode(newVectorTaskCode);
                vectorBioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(vectorBioTaskDtlTb));
                bioTaskDtlTbMapper.updateById(vectorBioTaskDtlTb);
            });
        }
        //修改质粒质检中实施方案编号
        List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        if (CollectionUtil.isNotEmpty(cerPlasmidQualityTbList)) {
            cerPlasmidQualityTbList.forEach(cerPlasmidQualityTb -> {
                log.info("修改质粒质检中实施方案编号");
                cerPlasmidQualityTb.setVectorTaskCode(newVectorTaskCode);
                cerPlasmidQualityTb.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
                cerPlasmidQualityTb.setProjectCode(cerProjectTb.getProjectCode());
                cerPlasmidQualityTbMapper.updateById(cerPlasmidQualityTb);

                BioTaskDtlTb cerPlasmidQualityTbBioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerPlasmidQualityTb.getTaskNum());
                PlasmidDTO plasmidDTO = JSONUtil.toBean(cerPlasmidQualityTbBioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
                plasmidDTO.setProjectCode(projectCode);
                plasmidDTO.setSubProjectCode(subProjectCode);
                plasmidDTO.setVectorTaskCode(newVectorTaskCode);
                cerPlasmidQualityTbBioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plasmidDTO));
                bioTaskDtlTbMapper.updateById(cerPlasmidQualityTbBioTaskDtlTb);
            });
        }

        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        if (CollectionUtil.isNotEmpty(cerPlasmidQualityTbList)) {

            cerTransformTbList.forEach(cerTransformTb -> {
                log.info("修改转化中实施方案编号");
                cerTransformTb.setVectorTaskCode(newVectorTaskCode);
                cerTransformTb.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
                cerTransformTb.setProjectCode(cerProjectTb.getProjectCode());
                cerTransformTbMapper.updateById(cerTransformTb);

                BioTaskDtlTb cerTransformTbBioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerTransformTb.getTaskNum());
                TransformDTO transformDTO = JSONUtil.toBean(cerTransformTbBioTaskDtlTb.getTaskForm(), TransformDTO.class);
                transformDTO.setProjectCode(projectCode);
                transformDTO.setSubProjectCode(subProjectCode);
                transformDTO.setVectorTaskCode(newVectorTaskCode);
                cerTransformTbBioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(transformDTO));
                bioTaskDtlTbMapper.updateById(cerTransformTbBioTaskDtlTb);

            });
        }
        //移苗
        List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
        if (CollectionUtil.isNotEmpty(cerConversionAndTransRefList)) {
            cerConversionAndTransRefList.forEach(cerConversionAndTransRef -> {
                cerConversionAndTransRef.setVectorTaskCode(newVectorTaskCode);
                cerConversionAndTransRef.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
                cerConversionAndTransRef.setProjectCode(cerProjectTb.getProjectCode());
                cerConversionAndTransRefMapper.updateById(cerConversionAndTransRef);

                BioTaskDtlTb cerTransformTbBioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerConversionAndTransRef.getTaskNum());
                ConversionAndTransDTO conversionAndTransDTO = JSONUtil.toBean(cerTransformTbBioTaskDtlTb.getTaskForm(), ConversionAndTransDTO.class);
                if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getTransFormList())) {
                    conversionAndTransDTO.getTransFormList().forEach(transForm -> {
                        if (oldVectorTaskCode.equals(transForm.getVectorTaskCode())) {
                            transForm.setVectorTaskCode(newVectorTaskCode);
                        }
                    });
                }
                if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getSampleCodeList())) {
                    conversionAndTransDTO.getSampleCodeList().forEach(sampleCode -> {
                        if (oldVectorTaskCode.equals(sampleCode.getVectorTaskCode())) {
                            sampleCode.setVectorTaskCode(newVectorTaskCode);
                        }
                    });
                }
                cerTransformTbBioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(conversionAndTransDTO));
                bioTaskDtlTbMapper.updateById(cerTransformTbBioTaskDtlTb);


            });
        }
        //取样库
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
        if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
            bioSampleTestTbList.forEach(bioSampleTestTb -> {
                log.info("修改取样中实施方案编号");
                bioSampleTestTb.setVectorTaskCode(newVectorTaskCode);
                bioSampleTestTbMapper.updateById(bioSampleTestTb);
            });
        }
        //取样申请
        List<BioSampleApplyTb> bioSampleApplyTbList = bioSampleApplyTbMapper.selectSelective(null);
        if (CollectionUtil.isNotEmpty(bioSampleApplyTbList)) {
            bioSampleApplyTbList.forEach(bioSampleApplyTb -> {
                if (StringUtils.isNotEmpty(bioSampleApplyTb.getVectorTaskCodes()) && bioSampleApplyTb.getVectorTaskCodes().contains(oldVectorTaskCode)) {
                    log.info("修改取样申请中实施方案编号");
                    bioSampleApplyTb.setVectorTaskCodes(bioSampleApplyTb.getVectorTaskCodes().replace(oldVectorTaskCode, newVectorTaskCode));
                    bioSampleApplyTbMapper.updateById(bioSampleApplyTb);
                }
                BioTaskDtlTb bioSampleApplyDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(bioSampleApplyTb.getApplyNo());
                NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioSampleApplyDtlTb.getTaskForm(), NewSampleTestDTO.class);
                if (CollectionUtil.isNotEmpty(newSampleTestDTO.getFirstSampleApplyList())) {
                    newSampleTestDTO.getFirstSampleApplyList().forEach(firstSampleApply -> {
                        if (oldVectorTaskCode.equals(firstSampleApply.getVectorTaskCode())) {
                            firstSampleApply.setVectorTaskCode(newVectorTaskCode);
                        }

                    });
                }
                if (CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList())) {
                    newSampleTestDTO.getRepeatSampleApplyList().forEach(repeatSampleApply -> {
                        if (oldVectorTaskCode.equals(repeatSampleApply.getVectorTaskCode())) {
                            repeatSampleApply.setVectorTaskCode(newVectorTaskCode);
                        }
                    });
                }
                bioSampleApplyDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
                bioTaskDtlTbMapper.updateById(bioSampleApplyDtlTb);


            });
        }

        List<PlantApplyDetailTb> plantApplyDetailTbList = plantApplyDetailTbMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
        if (CollectionUtil.isNotEmpty(plantApplyDetailTbList)) {
            log.info("修改CER种植申请中实施方案编号");
            plantApplyDetailTbList.forEach(plantApplyDetailTb -> {
                plantApplyDetailTb.setVectorTaskCode(oldVectorTaskCode);
                plantApplyDetailTbMapper.updateById(plantApplyDetailTb);
            });
            plantApplyDetailTbList.stream().map(PlantApplyDetailTb::getPlantApplyNum).distinct().forEach(plantApplyNum -> {
                PlantApplyTb plantApplyTb = plantApplyTbMapper.selectOneByPlantApplyNum(plantApplyNum);
                List<String> vectorTaskCodeList = JSONUtil.toList(plantApplyTb.getVectorTaskCodes(), String.class);
                vectorTaskCodeList.remove(oldVectorTaskCode);
                vectorTaskCodeList.add(newVectorTaskCode);
                plantApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(vectorTaskCodeList));
                plantApplyTbMapper.updateById(plantApplyTb);

                BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(plantApplyNum);
                PlantExperimentTaskDTO plantExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantExperimentTaskDTO.class);
                plantExperimentTaskDTO.setVectorTaskCodeList(vectorTaskCodeList);
                bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plantExperimentTaskDTO));
                bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
            });

        }

        List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
        if (CollectionUtil.isNotEmpty(plantSingleStockTbList)) {
            plantSingleStockTbList.forEach(plantSingleStockTb -> {
                plantSingleStockTb.setVectorTaskCode(newVectorTaskCode);
                plantSingleStockTbMapper.updateById(plantSingleStockTb);
            });
        }
        List<PlantMultipleStockTb> plantMultipleStockTbList = plantMultipleStockTbMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
        if (CollectionUtil.isNotEmpty(plantMultipleStockTbList)) {
            plantMultipleStockTbList.forEach(plantMultipleStockTb -> {
                plantMultipleStockTb.setVectorTaskCode(newVectorTaskCode);
                plantMultipleStockTbMapper.updateById(plantMultipleStockTb);
            });
        }

        //修改种子库
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
        if (CollectionUtil.isNotEmpty(seedStockTbList)) {
            log.info("修改种子库中实施方案编号");
            seedStockTbList.forEach(seedStockTb -> {
                seedStockTb.setVectorTaskCode(newVectorTaskCode);
                seedStockTbMapper.updateById(seedStockTb);
            });
        }

        BioSampleCodePrefixTb bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByVectorTaskCode(oldVectorTaskCode);
        if (bioSampleCodePrefixTb != null) {
            log.info("修改取样移苗前缀实施方案编号");
            bioSampleCodePrefixTb.setVectorTaskCode(newVectorTaskCode);
            bioSampleCodePrefixTbMapper.updateById(bioSampleCodePrefixTb);
        }
        //tc
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
        if (CollectionUtil.isNotEmpty(tcExperimentDesignTbList)) {
            tcExperimentDesignTbList.forEach(tcExperimentDesignTb -> {
                tcExperimentDesignTb.setVectorTaskCode(newVectorTaskCode);
                tcExperimentDesignTbMapper.updateById(tcExperimentDesignTb);
            });
            tcExperimentDesignTbList.stream().map(TcExperimentDesignTb::getExperimentNum).distinct().forEach(tcExperimentNum -> {
                TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcExperimentNum);
                List<String> vectorTaskCodeList = JSONUtil.toList(tcExperimentTb.getVectorTaskCodes(), String.class);
                vectorTaskCodeList.remove(oldVectorTaskCode);
                vectorTaskCodeList.add(newVectorTaskCode);
                tcExperimentTb.setVectorTaskCodes(JSONUtil.toJsonStr(vectorTaskCodeList));
                tcExperimentTbMapper.updateById(tcExperimentTb);

                BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcExperimentNum);
                TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
                tcExperimentTaskDTO.setVectorTaskCodeList(vectorTaskCodeList);
                bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcExperimentTaskDTO));
                bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
            });
        }
    }
}
