package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.SeedStockInLog;
import com.bio.drqi.domain.SeedStockOutLog;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.enums.DataPermissionTypeEnum;
import com.bio.drqi.enums.DataPermissionValueEnum;
import com.bio.drqi.enums.SeedOperateEnum;
import com.bio.drqi.manage.service.seed.SeedStoreService;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.SeedStockInLogMapper;
import com.bio.drqi.mapper.SeedStockOutLogMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.bio.drqi.seed.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SeedStoreServiceServiceImpl implements SeedStoreService {

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;

    @Resource
    private SeedStockOutLogMapper seedStockOutLogMapper;

    @Override
    public SeedDetailRspDTO querySeedByNum(String seedNum) {
        SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedNum);
        if (seedStockTb == null) {
            throw new BusinessException("不存在此种子信息");
        }
        SeedDetailRspDTO seedDetailRspDTO = BeanUtils.copyProperties(seedStockTb, SeedDetailRspDTO.class);
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
                result.add(seedOperateDetailRspDTO);
            }
        }
        return result;
    }


    private PageInfo<SeedStockPageRspDTO> getSeedStockPageRspDTOPageInfo(SeedStockPageReqDTO seedStockPageReqDTO, Boolean notEmptySeedNumberFlag) {
        PageInfo<SeedStockPageRspDTO> resultPage = new PageInfo<>(new ArrayList<SeedStockPageRspDTO>());
        ResponseResult<UserDetailRspDTO> responseResult = remoteUserService.queryUserById(SecurityContextHolder.getUserId());
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        List<UserDetailRspDTO.DataPermissionConfig> dataPermissionList = responseResult.getData().getDataPermissionConfigList();
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, CerBreedDict> cerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getSpeciesCode() + ":" + cerBreedDict.getBreedCode(), cerBreedDict -> cerBreedDict));
        dataPermissionList = dataPermissionList.stream().filter(dataPermission -> dataPermission.getPermissionType().equals(DataPermissionTypeEnum.SEED_STORE.name())).collect(Collectors.toList());
        SeedStockTb seedStockTb = new SeedStockTb();
        seedStockTb.setSeedNum(seedStockPageReqDTO.getSeedNum());
        seedStockTb.setProjectCode(seedStockPageReqDTO.getProjectCode());
        seedStockTb.setGeneration(seedStockPageReqDTO.getGeneration());
        seedStockTb.setSpecies(seedStockPageReqDTO.getSpecies());
        seedStockTb.setBreedCode(seedStockPageReqDTO.getBreedCode());
        seedStockTb.setHarvestType(seedStockPageReqDTO.getHarvestType());
        seedStockTb.setSourceType(seedStockPageReqDTO.getSourceType());
        seedStockTb.setStockLocationNum(seedStockPageReqDTO.getStockLocationNum());
        seedStockTb.setProductionLocationName(seedStockPageReqDTO.getProductionLocationName());
        seedStockTb.setSeedType(seedStockPageReqDTO.getSeedType());
        seedStockTb.setParentNum(seedStockPageReqDTO.getParentNum());
        seedStockTb.setPollinationMethod(seedStockPageReqDTO.getPollinationMethod());
        seedStockTb.setPlantNum(seedStockPageReqDTO.getPlantNum());
        seedStockTb.setBeninHarvestTime(seedStockPageReqDTO.getBeninHarvestTime());
        seedStockTb.setEndHarvestTime(seedStockPageReqDTO.getEndHarvestTime());
        seedStockTb.setGeneType(seedStockPageReqDTO.getGeneType());
        seedStockTb.setGeneticCharacter(seedStockPageReqDTO.getGeneticCharacter());
        seedStockTb.setAliasName(seedStockPageReqDTO.getAliasName());
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
            CerBreedDict cerBreedDict = cerBreedDictMap.get(seedStockPageRspDTO.getSpecies() + ":" + seedStockPageRspDTO.getBreedCode());
            seedStockPageRspDTO.setBreedName(Objects.nonNull(cerBreedDict) ? cerBreedDict.getBreedName() : null);
        });
        return targetPageInfo;
    }

}
