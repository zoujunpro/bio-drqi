package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.drqi.enums.DataPermissionTypeEnum;
import com.bio.drqi.enums.DataPermissionValueEnum;
import com.bio.drqi.manage.seed.SeedStockOutReqDTO;
import com.bio.drqi.manage.seed.SeedStockOutRspDTO;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.SeedStockOutLog;
import com.bio.drqi.manage.service.seed.SeedStockOutService;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.SeedStockOutLogMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeedStockOutServiceImpl implements SeedStockOutService {

    @Resource
    private SeedStockOutLogMapper seedStockOutLogMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private RemoteUserService remoteUserService;

    @Override
    public PageInfo<SeedStockOutRspDTO> listPage(SeedStockOutReqDTO seedStockOutReqDTO) {
        PageHelper.startPage(seedStockOutReqDTO.getPageNum(), seedStockOutReqDTO.getPageSize());
        ResponseResult<UserDetailRspDTO> responseResult = remoteUserService.queryUserById(SecurityContextHolder.getUserId());
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        List<UserDetailRspDTO.DataPermissionConfig> dataPermissionList = responseResult.getData().getDataPermissionConfigList();
        dataPermissionList = dataPermissionList.stream().filter(dataPermission -> dataPermission.getPermissionType().equals(DataPermissionTypeEnum.SEED_OUT.name())).collect(Collectors.toList());
        SeedStockOutLog seedStockOutLog = new SeedStockOutLog();
        seedStockOutLog.setSeedNum(seedStockOutReqDTO.getSeedNum());
        seedStockOutLog.setUseToCode(seedStockOutReqDTO.getUseToCode());
        seedStockOutLog.setTaskNum(seedStockOutReqDTO.getTaskNum());
        seedStockOutLog.setId(seedStockOutReqDTO.getId());
        seedStockOutLog.setOutTaskNum(seedStockOutReqDTO.getOutTaskNum());
        seedStockOutLog.setUseToDesc(seedStockOutReqDTO.getUseToDesc());
        seedStockOutLog.setSourceType(seedStockOutReqDTO.getSourceType());
        seedStockOutLog.setApplyUserId(seedStockOutReqDTO.getApplyUserId());
        seedStockOutLog.setApplyUserName(seedStockOutReqDTO.getApplyUserName());
        seedStockOutLog.setPlantCode(seedStockOutReqDTO.getPlantCode());
        seedStockOutLog.setParentNum(seedStockOutReqDTO.getParentNum());
        seedStockOutLog.setFatherInfo(seedStockOutReqDTO.getFatherInfo());
        seedStockOutLog.setMatherInfo(seedStockOutReqDTO.getMatherInfo());
        seedStockOutLog.setGeneration(seedStockOutReqDTO.getGeneration());
        seedStockOutLog.setSpeciesCode(seedStockOutReqDTO.getSpeciesCode());
        seedStockOutLog.setBreedCode(seedStockOutReqDTO.getBreedCode());
        seedStockOutLog.setPollinationMethod(seedStockOutReqDTO.getPollinationMethod());
        seedStockOutLog.setHarvestType(seedStockOutReqDTO.getHarvestType());
        seedStockOutLog.setHarvestTime(seedStockOutReqDTO.getHarvestTime());
        seedStockOutLog.setProductionLocationCode(seedStockOutReqDTO.getProductionLocationCode());
        seedStockOutLog.setStockLocationNum(seedStockOutReqDTO.getStockLocationNum());
        seedStockOutLog.setTargetCharacter(seedStockOutReqDTO.getTargetCharacter());
        seedStockOutLog.setAliasName(seedStockOutReqDTO.getAliasName());
        seedStockOutLog.setGeneType(seedStockOutReqDTO.getGeneType());
        seedStockOutLog.setMaterialType(seedStockOutReqDTO.getMaterialType());
        seedStockOutLog.setMatherSeedNum(seedStockOutReqDTO.getMatherSeedNum());
        seedStockOutLog.setFatherSeedNum(seedStockOutReqDTO.getFatherSeedNum());
        seedStockOutLog.setMatherRegionNum(seedStockOutReqDTO.getMatherRegionNum());
        seedStockOutLog.setFatherRegionNum(seedStockOutReqDTO.getFatherRegionNum());
        seedStockOutLog.setGenealogy(seedStockOutReqDTO.getGenealogy());
        seedStockOutLog.setGeneSeparateFlag(seedStockOutReqDTO.getGeneSeparateFlag());
        seedStockOutLog.setTransFlag(seedStockOutReqDTO.getTransFlag());
        seedStockOutLog.setVectorTaskCode(seedStockOutReqDTO.getVectorTaskCode());
        seedStockOutLog.setExperimentNum(seedStockOutReqDTO.getExperimentNum());
        seedStockOutLog.setProjectCode(seedStockOutReqDTO.getProjectCode());
        seedStockOutLog.setFatherSingleNum(seedStockOutReqDTO.getFatherSingleNum());
        seedStockOutLog.setMatherSingleNum(seedStockOutReqDTO.getMatherSingleNum());
        seedStockOutLog.setPdImplementCode(seedStockOutReqDTO.getPdImplementCode());
        seedStockOutLog.setBeginDate(seedStockOutReqDTO.getBeginDate());
        seedStockOutLog.setEndDate(seedStockOutReqDTO.getEndDate());
        if(CollectionUtil.isNotEmpty(dataPermissionList)&& DataPermissionValueEnum.OWNER.value.equals(dataPermissionList.get(0).getPermissionValue())){
            seedStockOutLog.setApplyUserId(SecurityContextHolder.getUserId());
        }
        List<SeedStockOutLog> seedStockOutLogList = seedStockOutLogMapper.selectSelective(seedStockOutLog);
        PageInfo<SeedStockOutLog> srcPageInfo = new PageInfo<>(seedStockOutLogList);
        PageInfo<SeedStockOutRspDTO> pageInfo= BeanUtils.copyPageInfoProperties(srcPageInfo, SeedStockOutRspDTO.class);
        fillSpeciesAndBreedName(pageInfo.getList());
        return pageInfo;
    }

    private void fillSpeciesAndBreedName(List<SeedStockOutRspDTO> seedStockOutRspDTOList) {
        if (CollectionUtil.isEmpty(seedStockOutRspDTOList)) {
            return;
        }
        Map<String, String> speciesMap = cerSpeciesConfMapper.selectList(null).stream()
                .collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName, (a, b) -> a));
        Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream()
                .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (a, b) -> a));
        for (SeedStockOutRspDTO seedStockOutRspDTO : seedStockOutRspDTOList) {
            seedStockOutRspDTO.setSpeciesName(speciesMap.get(seedStockOutRspDTO.getSpeciesCode()));
            seedStockOutRspDTO.setBreedName(cerBreedDictMap.get(seedStockOutRspDTO.getBreedCode()));
        }
    }
}
