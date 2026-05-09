package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.DataPermissionTypeEnum;
import com.bio.drqi.enums.DataPermissionValueEnum;
import com.bio.drqi.enums.SeedOperateEnum;
import com.bio.drqi.enums.SeedTaskTypeEnum;
import com.bio.drqi.manage.dto.seed.DownSpotCheckResultExcelDTO;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.manage.dto.seed.SeedOutDTO;
import com.bio.drqi.manage.seed.*;
import com.bio.drqi.manage.seedtask.SeedInDataReqDTO;
import com.bio.drqi.manage.seedtask.SeedTaskSeedNumRspDTO;
import com.bio.drqi.manage.service.seed.SeedStoreService;
import com.bio.drqi.mapper.*;
import com.bio.drqi.util.PaginationHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeedStoreServiceServiceImpl implements SeedStoreService {

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;

    @Resource
    private SeedStockOutLogMapper seedStockOutLogMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private BioDictMapper bioDictMapper;

    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private OssService ossService;

    @Override
    public SeedDetailRspDTO querySeedByNum(String seedNum) {
        SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedNum);
        if (seedStockTb == null) {
            throw new BusinessException("不存在此种子信息");
        }
        SeedDetailRspDTO seedDetailRspDTO = BeanUtils.copyProperties(seedStockTb, SeedDetailRspDTO.class);
        CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(seedDetailRspDTO.getBreedCode());
        if (cerBreedDict != null) {
            seedDetailRspDTO.setBreedName(cerBreedDict.getBreedName());
        }
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(seedDetailRspDTO.getSpeciesCode());
        if (cerSpeciesConf != null) {
            seedDetailRspDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
        }
        if (StringUtils.isNotEmpty(seedStockTb.getProductionLocationCode())) {
            SeedProduceAddressDict seedProduceAddressDict = seedProduceAddressDictMapper.selectOneByAddressCode(seedStockTb.getProductionLocationCode());
            seedDetailRspDTO.setProductionLocationName(seedProduceAddressDict == null ? null : seedProduceAddressDict.getAddressName());
        }

        return seedDetailRspDTO;
    }


    @Override
    public PageInfo<SeedStockPageRspDTO> listPage(SeedStockPageReqDTO seedStockPageReqDTO) {
        return getSeedStockPageRspDTOPageInfo(seedStockPageReqDTO, false);
    }


    @Override
    public PageInfo<SeedStockPageRspDTO> queryList(SeedStockPageReqDTO seedStockPageReqDTO) {
        return getSeedStockPageRspDTOPageInfo(seedStockPageReqDTO, true);
    }

    @Override
    public void moveStockLocationNum(List<MoveStockLocationNumReqDTO> moveStockLocationNumReqDTOList) {
        for (MoveStockLocationNumReqDTO moveStockLocationNumReqDTO : moveStockLocationNumReqDTOList) {
            SeedStockTb seedStockTb = seedStockTbMapper.selectById(moveStockLocationNumReqDTO.getId());
            if (seedStockTb.getSeedNumber().compareTo(new BigDecimal(0)) > 0) {
                seedStockTb.setStockLocationNum(moveStockLocationNumReqDTO.getStockLocationNum());
                seedStockTbMapper.updateById(seedStockTb);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void aliasName(AliasNameSeedReqDTO aliasNameSeedReqDTO) {
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectAllBySeedNumIn(aliasNameSeedReqDTO.getSeedNumList());
        if (CollectionUtil.isNotEmpty(seedStockTbList)) {
            seedStockTbList.forEach(seedStockTb -> {
                seedStockTb.setAliasName(aliasNameSeedReqDTO.getAliasName());
                seedStockTbMapper.updateById(seedStockTb);
            });
        }
    }

    @Override
    public List<SeedOperateDetailRspDTO> seedOperateDetail(String seedNum) {
        List<SeedOperateDetailRspDTO> result = new ArrayList<>();
        SeedStockInLog seedStockInLog = seedStockInLogMapper.selectOneBySeedNum(seedNum);
        if (seedStockInLog != null) {
            SeedOperateDetailRspDTO seedOperateDetailRspDTO = new SeedOperateDetailRspDTO();
            seedOperateDetailRspDTO.setOperateDesc(SeedOperateEnum.in.desc);
            seedOperateDetailRspDTO.setOperateCode(SeedOperateEnum.in.code);
            seedOperateDetailRspDTO.setOperateUserName(seedStockInLog.getApplyUserName());
            seedOperateDetailRspDTO.setOperateTime(seedStockInLog.getCreateTime());
            seedOperateDetailRspDTO.setUnit(seedStockInLog.getUnit());
            seedOperateDetailRspDTO.setTaskNum(seedStockInLog.getTaskNum());
            seedOperateDetailRspDTO.setNumber(seedStockInLog.getSeedNumber());
            result.add(seedOperateDetailRspDTO);
        }
        List<SeedStockOutLog> seedStockOutLogList = seedStockOutLogMapper.selectAllBySeedNum(seedNum);
        if (CollectionUtil.isNotEmpty(seedStockOutLogList)) {
            for (SeedStockOutLog seedStockOutLog : seedStockOutLogList) {
                SeedOperateDetailRspDTO seedOperateDetailRspDTO = new SeedOperateDetailRspDTO();
                seedOperateDetailRspDTO.setOperateDesc(SeedOperateEnum.out.desc);
                seedOperateDetailRspDTO.setOperateCode(SeedOperateEnum.out.code);
                seedOperateDetailRspDTO.setOperateUserName(seedStockOutLog.getApplyUserName());
                seedOperateDetailRspDTO.setOperateTime(seedStockOutLog.getCreateTime());
                seedOperateDetailRspDTO.setUnit(seedStockOutLog.getUnit());
                seedOperateDetailRspDTO.setNumber(seedStockOutLog.getSeedNumber());
                seedOperateDetailRspDTO.setTaskNum(seedStockOutLog.getTaskNum());
                result.add(seedOperateDetailRspDTO);
            }
        }
        return result;
    }


    @Override
    public PageInfo<SeedInStoreDTO.ExecuteFormContent> seedInData(SeedInDataReqDTO seedInDataReqDTO) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(seedInDataReqDTO.getTaskNum());
        SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedInStoreDTO.class);
        List<SeedInStoreDTO.ExecuteFormContent> executeFormContentList = seedInStoreDTO.getExecuteForm().getExecuteFormContentList();
        PaginationHelper paginationHelper = new PaginationHelper(executeFormContentList, seedInDataReqDTO.getPageNum(), seedInDataReqDTO.getPageSize());
        executeFormContentList = paginationHelper.getCurrentPageData();
        PageInfo<SeedInStoreDTO.ExecuteFormContent> pageInfo = new PageInfo<>();
        pageInfo.setList(BeanUtils.copyToList(executeFormContentList, SeedInStoreDTO.ExecuteFormContent.class));
        pageInfo.setTotal(paginationHelper.getTotalNum());
        return pageInfo;
    }


    @Override
    public List<SeedTaskSeedNumRspDTO> findAllSeedNum(String taskNum) {
        List<SeedTaskSeedNumRspDTO> result = new ArrayList<>();
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(taskNum);
        if (SeedTaskTypeEnum.seed_out_apply.name().equals(bioTaskDtlTb.getTaskTypeCode())) {
            SeedOutDTO seedOutDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedOutDTO.class);
            for (SeedOutDTO.ExecuteFormContent executeFormContent : seedOutDTO.getExecuteForm().getExecuteFormContentList()) {
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(executeFormContent.getSeedNum());
                result.add(new SeedTaskSeedNumRspDTO(seedStockTb.getSeedNum(), seedStockTb.getUnit(), null, executeFormContent.getNum()));
            }
        } else if (SeedTaskTypeEnum.seed_store_apply.name().equals(bioTaskDtlTb.getTaskTypeCode())) {
            List<SeedStockInLog> seedStockInLogList = seedStockInLogMapper.selectAllByTaskNum(bioTaskDtlTb.getTaskNum());
            for (SeedStockInLog seedStockInLog : seedStockInLogList) {
                result.add(new SeedTaskSeedNumRspDTO(seedStockInLog.getSeedNum(), seedStockInLog.getUnit(), seedStockInLog.getId(), seedStockInLog.getSeedNumber().toString()));
            }
        }
        return result;
    }

    @Override
    public SeedMapRspDTO findSeedMap(String seedNum) {
        SeedMapRspDTO seedMapRspDTO = new SeedMapRspDTO();
        Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        buildSeedMapRspDTO(seedNum, seedMapRspDTO, cerBreedDictMap);
        return seedMapRspDTO;
    }

    private void buildSeedMapRspDTO(String seedNum, SeedMapRspDTO seedMapRspDTO, Map<String, String> cerBreedDictMap) {
        SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedNum);
        if (seedStockTb == null) {
            throw new BusinessException("异常种子编号,库存中无此种子编号:" + seedNum);
        }


        seedMapRspDTO.buildMap(buildSeedMapDTO(seedNum, cerBreedDictMap), buildSeedMapDTO(seedStockTb.getFatherSeedNum(), cerBreedDictMap), buildSeedMapDTO(seedStockTb.getMatherSeedNum(), cerBreedDictMap));
        if (StringUtils.isNotEmpty(seedStockTb.getFatherSeedNum())) {
            buildSeedMapRspDTO(seedStockTb.getFatherSeedNum(), seedMapRspDTO, cerBreedDictMap);
        }
        if (StringUtils.isNotEmpty(seedStockTb.getMatherSeedNum())) {
            buildSeedMapRspDTO(seedStockTb.getMatherSeedNum(), seedMapRspDTO, cerBreedDictMap);
        }

    }

    private SeedMapRspDTO.SeedMapDTO buildSeedMapDTO(String seedNum, Map<String, String> cerBreedDictMap) {
        SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedNum);
        if (seedStockTb != null) {
            BioDict bioDict = bioDictMapper.selectOneByDictTypeAndDictValueCode(BioDictTypeEnum.POLLINATE_TYPE.name(), seedStockTb.getPollinationMethod());
            SeedMapRspDTO.SeedMapDTO seedMapDTO = new SeedMapRspDTO.SeedMapDTO();
            seedMapDTO.setSeedNum(seedNum);
            seedMapDTO.setVectorTaskCode(seedStockTb.getVectorTaskCode() == null ? "" : seedStockTb.getVectorTaskCode());
            seedMapDTO.setGeneration(seedStockTb.getGeneration());
            seedMapDTO.setPollinationMethod(bioDict != null ? bioDict.getDictValueName() : "");
            seedMapDTO.setPollinationMethodCode(bioDict != null ? bioDict.getDictValueCode() : "");
            seedMapDTO.setBreedName(cerBreedDictMap.get(seedStockTb.getBreedCode()));
            return seedMapDTO;
        } else {
            return null;
        }

    }

    @Override
    public void remark(SeedStockRemarkReqDTO seedStockRemarkReqDTO) {
        SeedStockTb seedStockTb = seedStockTbMapper.selectById(seedStockRemarkReqDTO.getId());
        if (seedStockTb == null) {
            throw new BusinessException("参数异常，不存在此种子");
        }
        seedStockTbMapper.updateRemarksById(seedStockRemarkReqDTO.getRemarks(), seedStockTb.getId());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void spotCheckResult(SeedStockSpotCheckResultReqDTO seedStockSpotCheckResultReqDTO) {
        if (CollectionUtil.isNotEmpty(seedStockSpotCheckResultReqDTO.getContentList())) {
            seedStockSpotCheckResultReqDTO.getContentList().forEach(content -> {
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(content.getSeedNum());
                if (seedStockTb == null) {
                    throw new BusinessException("找不到种子信息：" + content.getSeedNum());
                }
                seedStockTbMapper.updateSpotCheckResultById(StringUtils.isEmpty(seedStockTb.getSpotCheckResult()) ? content.getSpotCheckResult() : seedStockTb.getSpotCheckResult() + ";" + content.getSpotCheckResult(), seedStockTb.getId());
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadSpotCheckResultExcel(SeedStockUploadSpotCheckResultExcelReqDTO seedStockUploadSpotCheckResultExcelReqDTO) {
        String excelUrl = seedStockUploadSpotCheckResultExcelReqDTO.getExcelUrl();
        if (StringUtils.isEmpty(excelUrl) || (!excelUrl.endsWith(".xlsx") && !excelUrl.endsWith(".xls"))) {
            throw new BusinessException("请上传excel文件");
        }
        File tempFile = FileUtil.createTempFile("seed-spot-check-" + System.currentTimeMillis(), true);
        try {
            ossService.downloadPath(tempFile.getAbsolutePath(), excelUrl);
        } catch (Exception e) {
            log.error("【种子抽检反馈文件处理失败】下载OSS文件失败, excelUrl={}", excelUrl, e);
            throw new BusinessException("文件处理异常");
        }
        try {
            List<DownSpotCheckResultExcelDTO> list = ExcelUtil.readExcel(tempFile.getAbsolutePath(), DownSpotCheckResultExcelDTO.class);
            if (CollectionUtil.isNotEmpty(list)) {
                Set<String> seedNumSet = new HashSet<>();
                list.forEach(downSpotCheckResultExcelDTO -> {
                    if (StringUtils.isEmpty(downSpotCheckResultExcelDTO.getSeedNum()) && StringUtils.isEmpty(downSpotCheckResultExcelDTO.getSpotCheckResult())) {
                        return;
                    }
                    if (StringUtils.isEmpty(downSpotCheckResultExcelDTO.getSeedNum())) {
                        throw new BusinessException("上传数据异常，存在未填写种子编号的数据");
                    }
                    if (!seedNumSet.add(downSpotCheckResultExcelDTO.getSeedNum())) {
                        throw new BusinessException("excel中存在重复的种子编号：" + downSpotCheckResultExcelDTO.getSeedNum());
                    }
                    SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(downSpotCheckResultExcelDTO.getSeedNum());
                    if (seedStockTb == null) {
                        throw new BusinessException("找不到种子信息：" + downSpotCheckResultExcelDTO.getSeedNum());
                    }
                    seedStockTbMapper.updateSpotCheckResultById(StringUtils.isEmpty(seedStockTb.getSpotCheckResult()) ? downSpotCheckResultExcelDTO.getSpotCheckResult() : seedStockTb.getSpotCheckResult() + ";" + downSpotCheckResultExcelDTO.getSpotCheckResult(), seedStockTb.getId());
                });
            }
        } finally {
            FileUtil.del(tempFile);
        }
    }

    @Override
    public void downSpotCheckResultExcel(HttpServletResponse httpServletResponse) {
        ExcelUtil.writeExcel("抽检反馈模板", "sheet1", null, DownSpotCheckResultExcelDTO.class, httpServletResponse);
    }

    @Override
    public List<String> queryChildSeed(String seedNum) {
        List<String> result = new ArrayList<>();
        List<SeedStockTb> matherSeedStockTbList = seedStockTbMapper.selectAllByMatherSeedNum(seedNum);
        List<SeedStockTb> fatherSeedStockTbList = seedStockTbMapper.selectAllByFatherSeedNum(seedNum);
        if (CollectionUtil.isNotEmpty(matherSeedStockTbList)) {
            result.addAll(matherSeedStockTbList.stream().map(SeedStockTb::getSeedNum).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(fatherSeedStockTbList)) {
            result.addAll(fatherSeedStockTbList.stream().map(SeedStockTb::getSeedNum).collect(Collectors.toList()));
        }
        return result.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<SeedStockQueryPlantListRspDTO> queryPlantList(String seedNum) {
        List<SeedStockQueryPlantListRspDTO> resultList = new ArrayList<>();
        List<PlantApplyDetailTb> plantApplyDetailTbList = plantApplyDetailTbMapper.selectAllBySeedNum(seedNum);
        if (CollectionUtil.isNotEmpty(plantApplyDetailTbList)) {
            plantApplyDetailTbList.forEach(plantApplyDetailTb -> {
                SeedStockQueryPlantListRspDTO seedStockQueryPlantListRspDTO = new SeedStockQueryPlantListRspDTO();
                seedStockQueryPlantListRspDTO.setTaskNum(plantApplyDetailTb.getPlantApplyNum());
                seedStockQueryPlantListRspDTO.setSourceCode(SourceCodeEnum.cer.name());
                seedStockQueryPlantListRspDTO.setPlantUserName(plantApplyDetailTb.getCreateUserName());
                seedStockQueryPlantListRspDTO.setCreateTime(plantApplyDetailTb.getCreateTime());
                seedStockQueryPlantListRspDTO.setRegionCode(plantApplyDetailTb.getRegionNum());
                seedStockQueryPlantListRspDTO.setSeedNum(plantApplyDetailTb.getSeedNum());
                seedStockQueryPlantListRspDTO.setPlantNumber(plantApplyDetailTb.getPlantNumber() + plantApplyDetailTb.getPlantUnit());
                resultList.add(seedStockQueryPlantListRspDTO);
            });
        }
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllBySeedNum(seedNum);
        if (CollectionUtil.isNotEmpty(tcExperimentDesignTbList)) {
            tcExperimentDesignTbList.forEach(tcExperimentDesignTb -> {
                SeedStockQueryPlantListRspDTO seedStockQueryPlantListRspDTO = new SeedStockQueryPlantListRspDTO();
                seedStockQueryPlantListRspDTO.setTaskNum(tcExperimentDesignTb.getTaskNum());
                seedStockQueryPlantListRspDTO.setSourceCode(SourceCodeEnum.field.name());
                seedStockQueryPlantListRspDTO.setPlantUserName(tcExperimentDesignTb.getCreateUserName());
                seedStockQueryPlantListRspDTO.setCreateTime(tcExperimentDesignTb.getCreateTime());
                seedStockQueryPlantListRspDTO.setRegionCode(tcExperimentDesignTb.getRegionNum());
                seedStockQueryPlantListRspDTO.setSeedNum(tcExperimentDesignTb.getSeedNum());
                seedStockQueryPlantListRspDTO.setPlantNumber(tcExperimentDesignTb.getSeedingNumber() + tcExperimentDesignTb.getSeedingUnit());
                resultList.add(seedStockQueryPlantListRspDTO);
            });
        }

        return resultList;
    }

    @Override
    public List<SeedStockQueryListRspDTO> queryUserList() {
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectSubmitUserIdAndSubmitUserName();
        if (CollectionUtil.isNotEmpty(seedStockTbList)) {
            return BeanUtils.copyListProperties(seedStockTbList, SeedStockQueryListRspDTO.class).stream().filter(seedStockQueryListRspDTO -> seedStockQueryListRspDTO.getSubmitUserId() != null).collect(Collectors.toList());
        }
        return null;

    }


    private PageInfo<SeedStockPageRspDTO> getSeedStockPageRspDTOPageInfo(SeedStockPageReqDTO seedStockPageReqDTO, Boolean notEmptySeedNumberFlag) {
        PageInfo<SeedStockPageRspDTO> resultPage = new PageInfo<>(new ArrayList<SeedStockPageRspDTO>());
        ResponseResult<UserDetailRspDTO> responseResult = remoteUserService.queryUserById(SecurityContextHolder.getUserId());
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        List<UserDetailRspDTO.DataPermissionConfig> dataPermissionList = responseResult.getData().getDataPermissionConfigList();
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        List<SeedProduceAddressDict> seedProduceAddressDictList = seedProduceAddressDictMapper.selectAll();
        Map<String, String> seedProduceAddressDictMap = seedProduceAddressDictList.stream().collect(Collectors.toMap(SeedProduceAddressDict::getAddressCode, SeedProduceAddressDict::getAddressName));

        Map<String, String> speciesMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        Map<String, String> cerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        dataPermissionList = dataPermissionList.stream().filter(dataPermission -> dataPermission.getPermissionType().equals(DataPermissionTypeEnum.SEED_STORE.name())).collect(Collectors.toList());
        SeedStockTb seedStockTb = new SeedStockTb();
        seedStockTb.setId(seedStockPageReqDTO.getId());
        seedStockTb.setSeedNum(seedStockPageReqDTO.getSeedNum());
        seedStockTb.setVectorTaskCode(seedStockPageReqDTO.getVectorTaskCode());
        seedStockTb.setGeneration(seedStockPageReqDTO.getGeneration());
        seedStockTb.setSpeciesCode(seedStockPageReqDTO.getSpecies());
        seedStockTb.setBreedCode(seedStockPageReqDTO.getBreedCode());
        seedStockTb.setHarvestType(seedStockPageReqDTO.getHarvestType());
        seedStockTb.setSourceType(seedStockPageReqDTO.getSourceType());
        seedStockTb.setStockLocationNum(seedStockPageReqDTO.getStockLocationNum());
        seedStockTb.setProductionLocationCode(seedStockPageReqDTO.getProductionLocationCode());
        seedStockTb.setParentNum(seedStockPageReqDTO.getParentNum());
        seedStockTb.setPollinationMethod(seedStockPageReqDTO.getPollinationMethod());
        seedStockTb.setPlantCode(seedStockPageReqDTO.getPlantCode());
        seedStockTb.setBeninHarvestTime(seedStockPageReqDTO.getBeninHarvestTime());
        seedStockTb.setEndHarvestTime(seedStockPageReqDTO.getEndHarvestTime());
        seedStockTb.setGeneType(seedStockPageReqDTO.getGeneType());
        seedStockTb.setTargetCharacter(seedStockPageReqDTO.getTargetCharacter());
        seedStockTb.setAliasName(seedStockPageReqDTO.getAliasName());
        seedStockTb.setFilterNullFlag(seedStockPageReqDTO.getFilterNullFlag());
        seedStockTb.setProjectCode(seedStockPageReqDTO.getProjectCode());
        seedStockTb.setMaterialType(seedStockPageReqDTO.getMaterialType());
        seedStockTb.setRemarks(seedStockPageReqDTO.getRemarks());
        seedStockTb.setPdImplementCode(seedStockPageReqDTO.getPdImplementCode());
        seedStockTb.setSubmitUserId(seedStockPageReqDTO.getSubmitUserId());
        seedStockTb.setMatherSeedNum(seedStockPageReqDTO.getMatherSeedNum());
        seedStockTb.setMatherSingleNum(seedStockPageReqDTO.getMatherSingleNum());
        if (seedStockPageReqDTO.getOrder() != null) {
            seedStockTb.setOrderField(seedStockPageReqDTO.getOrder().getFieldName());
            seedStockTb.setOrderType(seedStockPageReqDTO.getOrder().getOrderType());
        }

        if (StringUtils.isNotEmpty(seedStockPageReqDTO.getEndDate())) {
            seedStockTb.setEndDate(seedStockPageReqDTO.getEndDate().replace("-", ""));
        }
        if (StringUtils.isNotEmpty(seedStockPageReqDTO.getBeginDate())) {
            seedStockTb.setBeginDate(seedStockPageReqDTO.getBeginDate().replace("-", ""));
        }
        if (CollectionUtil.isNotEmpty(dataPermissionList) && DataPermissionValueEnum.OWNER.value.equals(dataPermissionList.get(0).getPermissionValue())) {
            seedStockTb.setSubmitUserId(SecurityContextHolder.getUserId());
        }
        PageHelper.startPage(seedStockPageReqDTO.getPageNum(), seedStockPageReqDTO.getPageSize());
        seedStockTb.setNotEmptySeedNumberFlag(notEmptySeedNumberFlag);
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectSelective(seedStockTb);
        if (CollectionUtil.isEmpty(seedStockTbList)) {
            return resultPage;
        }
        PageInfo<SeedStockTb> srcPageInfo = new PageInfo<>(seedStockTbList);
        PageInfo<SeedStockPageRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, SeedStockPageRspDTO.class);
        targetPageInfo.getList().forEach(seedStockPageRspDTO -> {
            seedStockPageRspDTO.setBreedName(cerBreedDictMap.get(seedStockPageRspDTO.getBreedCode()));
            seedStockPageRspDTO.setSpeciesName(speciesMap.get(seedStockPageRspDTO.getSpeciesCode()));
            seedStockPageRspDTO.setProductionLocationName(seedProduceAddressDictMap.get(seedStockPageRspDTO.getProductionLocationCode()));
        });
        return targetPageInfo;
    }

}
