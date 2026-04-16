package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.contents.CerProjectContents;

import com.bio.drqi.enums.*;
import com.bio.drqi.manage.seed.*;
import com.bio.drqi.manage.seedtask.SeedInDataReqDTO;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.manage.service.common.SeedPlantService;
import com.bio.drqi.manage.service.seed.SeedStockInService;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeedStockInServiceImpl implements SeedStockInService {

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;

    @Resource
    private OssService ossService;

    @Resource
    private BioDictMapper bioDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private SeedPlantService seedPlantService;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;


    @Override
    public PageInfo<SeedStockInRspDTO> listPage(SeedStockInReqDTO seedStockInReqDTO) {
        PageHelper.startPage(seedStockInReqDTO.getPageNum(), seedStockInReqDTO.getPageSize());
        ResponseResult<UserDetailRspDTO> responseResult = remoteUserService.queryUserById(SecurityContextHolder.getUserId());
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        List<UserDetailRspDTO.DataPermissionConfig> dataPermissionList = responseResult.getData().getDataPermissionConfigList();
        dataPermissionList = dataPermissionList.stream().filter(dataPermission -> dataPermission.getPermissionType().equals(DataPermissionTypeEnum.SEED_IN.name())).collect(Collectors.toList());
        SeedStockInLog seedStockInLog = new SeedStockInLog();
        seedStockInLog.setSeedNum(seedStockInReqDTO.getSeedNum());
        seedStockInLog.setSourceType(seedStockInReqDTO.getSourceType());
        seedStockInLog.setTaskNum(seedStockInReqDTO.getTaskNum());
        if (CollectionUtil.isNotEmpty(dataPermissionList) && DataPermissionValueEnum.OWNER.value.equals(dataPermissionList.get(0).getPermissionValue())) {
            seedStockInLog.setApplyUserId(SecurityContextHolder.getUserId());
        }
        List<SeedStockInLog> seedStockInLogList = seedStockInLogMapper.selectSelective(seedStockInLog);
        PageInfo<SeedStockInLog> srcPageInfo = new PageInfo<>(seedStockInLogList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, SeedStockInRspDTO.class);
    }

    @Override
    public List<ParseSeedInExcelRspDTO> parseSeedInExcel(ParseSeedInExcelReqDTO parseSeedInExcelReqDTO) {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + parseSeedInExcelReqDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, parseSeedInExcelReqDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        return parseExcelToExecuteFormContent(tempFilePath);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void store(SeedInStoreReqDTO seedInStoreReqDTO) {
        for (SeedInStoreReqDTO.Content content : seedInStoreReqDTO.getContentList()) {
            SeedStockInLog stockInLog = seedStockInLogMapper.selectOneByUniqueCode(content.getUniqueCode());
            if (stockInLog != null) {
                throw new BusinessException("此种子已经入库");
            }
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(seedInStoreReqDTO.getTaskNum());

            if (!BioTaskStatusEnum.TASK_STATUS_1.status.equals(bioTaskDtlTb.getTaskStatus())) {
                throw new BusinessException("不是执行中工单");
            }

            SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedInStoreDTO.class);

            Map<String, SeedInStoreDTO.ExecuteFormContent> executeFormContentMap = seedInStoreDTO.getExecuteForm().getExecuteFormContentList().stream().collect(Collectors.toMap(SeedInStoreDTO.ExecuteFormContent::getUniqueCode, executeFormContent -> executeFormContent));
            SeedInStoreDTO.ExecuteFormContent executeFormContent = executeFormContentMap.get(content.getUniqueCode());
            if (executeFormContent == null) {
                throw new BusinessException("此工单中无此种子");
            }
            SeedStockTb seedStockTb = new SeedStockTb();
            if (StringUtils.isNotEmpty(executeFormContent.getVectorTaskCode())) {
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(executeFormContent.getVectorTaskCode());
                CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
                seedStockTb.setTargetCharacter(cerProjectTb.getProjectName());
                seedStockTb.setProjectCode(cerVectorTaskTb.getProjectCode());
            }else {
                seedStockTb.setTargetCharacter(executeFormContent.getTargetCharacter());
            }
            seedStockTb.setVectorTaskCode(executeFormContent.getVectorTaskCode());
            seedStockTb.setPlantCode(executeFormContent.getPlantCode());
            seedStockTb.setGeneration(executeFormContent.getGeneration());
            seedStockTb.setSpeciesCode(executeFormContent.getSpeciesCode());
            seedStockTb.setBreedCode(executeFormContent.getBreedCode());
            seedStockTb.setPollinationMethod(executeFormContent.getPollinationMethod());
            seedStockTb.setHarvestType(executeFormContent.getHarvestType());
            seedStockTb.setHarvestTime(executeFormContent.getHarvestTime());
            seedStockTb.setSeedNumber(content.getSeedNumber());
            seedStockTb.setTotalNumber(content.getSeedNumber());
            seedStockTb.setUnit(executeFormContent.getUnit());
            seedStockTb.setSourceType(executeFormContent.getSource());
            seedStockTb.setProductionLocationCode(executeFormContent.getProductionLocationCode());
            seedStockTb.setSubmitUserId(bioTaskDtlTb.getApplyUserId());
            seedStockTb.setSubmitUserName(bioTaskDtlTb.getApplyUserName());
            seedStockTb.setCreateTime(new Date());
            seedStockTb.setUpdateTime(new Date());
            seedStockTb.setRemarks(executeFormContent.getRemarks());
            seedStockTb.setStockLocationNum(executeFormContent.getStockLocationNum());
            seedStockTb.setGeneType(executeFormContent.getGeneType());
            seedStockTb.setAliasName(executeFormContent.getAliasName());
            seedStockTb.setMaterialType(executeFormContent.getMaterialType());
            seedStockTb.setStockLocationNum(content.getStockLocationNum());
            seedStockTb.setExperimentNum(executeFormContent.getExperimentNum());
            seedStockTb.setTransFlag(executeFormContent.getTransFlag());
            seedStockTb.setMatherSeedNum(executeFormContent.getMatherSeedNum());
            seedStockTb.setFatherSeedNum(executeFormContent.getFatherSeedNum());
            seedStockTb.setGeneSeparateFlag(executeFormContent.getGeneSeparateFlag());
            seedStockTb.setFatherRegionNum(executeFormContent.getFatherRegionNum());
            seedStockTb.setMatherRegionNum(executeFormContent.getMatherRegionNum());
            seedStockTb.setFatherSingleNum(executeFormContent.getFatherSingleNum());
            seedStockTb.setMatherSingleNum(executeFormContent.getMatherSingleNum());
            seedStockTb.setFatherInfo(executeFormContent.getFatherInfo());
            seedStockTb.setMatherInfo(executeFormContent.getMatherInfo());
            seedStockTbMapper.insert(seedStockTb);
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(executeFormContent.getSpeciesCode());
            seedStockTb.setSeedNum(cerSpeciesConf.getNumPrefix() + StringUtils.padl(String.valueOf(seedStockTb.getId()), 8, '0'));
            seedStockTbMapper.updateById(seedStockTb);
            SeedStockInLog seedStockInLog = new SeedStockInLog();
            seedStockInLog.setSeedNum(seedStockTb.getSeedNum());
            seedStockInLog.setRemarks(seedStockTb.getRemarks());
            seedStockInLog.setUnit(executeFormContent.getUnit());
            seedStockInLog.setSeedNumber(content.getSeedNumber());
            seedStockInLog.setSourceType(executeFormContent.getSource());
            seedStockInLog.setTaskNum(seedInStoreReqDTO.getTaskNum());
            seedStockInLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
            seedStockInLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
            seedStockInLog.setCreateTime(new Date());
            seedStockInLog.setSeedStockTb(seedStockTb);
            seedStockInLog.setUniqueCode(executeFormContent.getUniqueCode());
            seedStockInLogMapper.insert(seedStockInLog);

            executeFormContent.setSeedNumber(content.getSeedNumber());
            executeFormContent.setStoreFlag(CerProjectContents.Y);
            executeFormContent.setStockLocationNum(content.getStockLocationNum());
            executeFormContent.setSeedNum(seedStockTb.getSeedNum());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(seedInStoreDTO, new JSONConfig().setIgnoreNullValue(false)));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

            seedPlantService.seedInStockAddRefPlant(seedStockTb);
        }

    }

    @Override
    public PageInfo<SeedInStoreDTO.ExecuteFormContent> seedInData(SeedInDataReqDTO seedInDataReqDTO) {
        return null;
    }

    @Override
    public void downSampleTemplate(HttpServletResponse response) {
        ExcelUtil.writeExcel("种子入库数据模板", "sheet1", new ArrayList<>(), com.bio.drqi.common.dto.SeedInStockExcelDTO.class, response);

    }


    private List<ParseSeedInExcelRspDTO> parseExcelToExecuteFormContent(String tempFilePath) {
        List<BioDict> bioDictList = bioDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectList(null);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<SeedProduceAddressDict> seedProduceAddressDictList = seedProduceAddressDictMapper.selectAll();
        Map<String, String> seedProduceAddressDictMap = seedProduceAddressDictList.stream().collect(Collectors.toMap(SeedProduceAddressDict::getAddressName, SeedProduceAddressDict::getAddressCode));
        Map<String, CerBreedDict> cerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getSpeciesCode() + ":" + cerBreedDict.getBreedName(), cerBreedDict -> cerBreedDict));
        Map<String, CerSpeciesConf> cerSpeciesConfMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesName, cerSpeciesConf -> cerSpeciesConf));
        Map<String, BioDict> bioDictMap = bioDictList.stream().collect(Collectors.toMap(bioDict -> bioDict.getDictType() + ":" + bioDict.getDictValueName(), bioDict -> bioDict));
        List<ParseSeedInExcelRspDTO> parseSeedInExcelRspDTOList = ExcelUtil.readExcel(tempFilePath, ParseSeedInExcelRspDTO.class);
        for (ParseSeedInExcelRspDTO parseSeedInExcelRspDTO : parseSeedInExcelRspDTOList) {
            log.info("种子入库：parseSeedInExcelRspDTO={}", JSONUtil.toJsonStr(parseSeedInExcelRspDTO));
            //翻译种子来源
            com.bio.drqi.common.enums.SeedSourceEnum seedSourceEnum = com.bio.drqi.common.enums.SeedSourceEnum.getByName(parseSeedInExcelRspDTO.getSource());
            if (seedSourceEnum == null) {
                throw new BusinessException("种子来源填写错误：" + parseSeedInExcelRspDTO.getSource());
            }
            parseSeedInExcelRspDTO.setSource(seedSourceEnum.code);

            //翻译收获方式
            if (StringUtils.isNotEmpty(parseSeedInExcelRspDTO.getHarvestType())) {
                BioDict harvestTypeBioDict = bioDictMap.get(BioDictTypeEnum.HARVEST_TYPE + ":" + parseSeedInExcelRspDTO.getHarvestType());
                if (harvestTypeBioDict == null) {
                    throw new BusinessException("收获方式填写错误：" + parseSeedInExcelRspDTO.getHarvestType());
                }
                parseSeedInExcelRspDTO.setHarvestType(harvestTypeBioDict.getDictValueCode());
            }
            //翻译授粉方式
            if (StringUtils.isNotEmpty(parseSeedInExcelRspDTO.getPollinationMethod())) {
                BioDict pollinationMethodBioDict = bioDictMap.get(BioDictTypeEnum.POLLINATE_TYPE + ":" + parseSeedInExcelRspDTO.getPollinationMethod());
                if (pollinationMethodBioDict == null) {
                    throw new BusinessException("授粉方式填写错误：" + parseSeedInExcelRspDTO.getPollinationMethod());
                }
                parseSeedInExcelRspDTO.setPollinationMethod(pollinationMethodBioDict.getDictValueCode());
            }
            //翻译物种
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMap.get(parseSeedInExcelRspDTO.getSpecieName());
            if (cerSpeciesConf == null) {
                throw new BusinessException("物种填写错误：" + parseSeedInExcelRspDTO.getSpecieName());
            }
            parseSeedInExcelRspDTO.setSpeciesCode(cerSpeciesConf.getSpeciesCode());
            parseSeedInExcelRspDTO.setSpecieName(cerSpeciesConf.getSpeciesName());
            //翻译品种
            CerBreedDict cerBreedDict = cerBreedDictMap.get(cerSpeciesConf.getSpeciesCode() + ":" + parseSeedInExcelRspDTO.getBreedName());
            if (cerBreedDict == null) {
                throw new BusinessException("品种填写错误：" + parseSeedInExcelRspDTO.getBreedName());
            }
            parseSeedInExcelRspDTO.setBreedName(cerBreedDict.getBreedName());
            parseSeedInExcelRspDTO.setBreedCode(cerBreedDict.getBreedCode());
            //翻译材料类型
            BioDict materialTypeBioDict = bioDictMap.get(BioDictTypeEnum.MATERIAL_TYPE + ":" + parseSeedInExcelRspDTO.getMaterialType());
            if (materialTypeBioDict == null) {
                throw new BusinessException("材料类型填写错误：" + parseSeedInExcelRspDTO.getMaterialType());
            }
            parseSeedInExcelRspDTO.setMaterialType(materialTypeBioDict.getDictValueCode());

            //翻译代次
            GenerationEnum generationEnum = GenerationEnum.getGeneration(parseSeedInExcelRspDTO.getGeneration());
            if (generationEnum == null) {
                throw new BusinessException("代次填写错误：" + parseSeedInExcelRspDTO.getGeneration());
            }
            parseSeedInExcelRspDTO.setGeneration(generationEnum.code);

            //校验生产地址
            if (StringUtils.isNotEmpty(parseSeedInExcelRspDTO.getProductionLocationName())) {
                if (seedProduceAddressDictMap.get(parseSeedInExcelRspDTO.getProductionLocationName()) == null) {
                    throw new BusinessException("生产地址填写错误：" + parseSeedInExcelRspDTO.getProductionLocationName());
                }
                parseSeedInExcelRspDTO.setProductionLocationCode(seedProduceAddressDictMap.get(parseSeedInExcelRspDTO.getProductionLocationName()));
            }

            parseSeedInExcelRspDTO.setStoreFlag(CerProjectContents.N);
            parseSeedInExcelRspDTO.setUniqueCode(UUID.fastUUID().toString().replace("-", ""));
        }

        return parseSeedInExcelRspDTOList;
    }

}
