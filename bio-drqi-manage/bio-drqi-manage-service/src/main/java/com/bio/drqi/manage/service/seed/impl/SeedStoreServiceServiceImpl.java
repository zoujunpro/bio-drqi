package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.DataPermissionTypeEnum;
import com.bio.drqi.enums.DataPermissionValueEnum;
import com.bio.drqi.enums.SeedOperateEnum;
import com.bio.drqi.enums.SeedTaskTypeEnum;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.manage.dto.seed.SeedOutDTO;
import com.bio.drqi.manage.service.seed.SeedStoreService;
import com.bio.drqi.mapper.*;
import com.bio.drqi.manage.seed.*;
import com.bio.drqi.manage.seedtask.SeedInDataReqDTO;
import com.bio.drqi.manage.seedtask.SeedTaskSeedNumRspDTO;
import com.bio.drqi.util.PaginationHelper;
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
            seedDetailRspDTO.setProductionLocationName(seedProduceAddressDict==null?null:seedProduceAddressDict.getAddressName());
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
    public void remark(SeedStockRemarkReqDTO seedStockRemarkReqDTO) {
        SeedStockTb seedStockTb = seedStockTbMapper.selectById(seedStockRemarkReqDTO.getId());
        if (seedStockTb == null) {
            throw new BusinessException("参数异常，不存在此种子");
        }
        seedStockTbMapper.updateRemarksById(seedStockRemarkReqDTO.getRemarks(), seedStockTb.getId());

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
        seedStockTb.setMaterialType(seedStockPageReqDTO.getMaterialType());
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
