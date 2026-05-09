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
import com.bio.drqi.domain.SeedStockOutLog;
import com.bio.drqi.manage.service.seed.SeedStockOutService;
import com.bio.drqi.mapper.SeedStockOutLogMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeedStockOutServiceImpl implements SeedStockOutService {

    @Resource
    private SeedStockOutLogMapper seedStockOutLogMapper;

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
        if(CollectionUtil.isNotEmpty(dataPermissionList)&& DataPermissionValueEnum.OWNER.value.equals(dataPermissionList.get(0).getPermissionValue())){
            seedStockOutLog.setApplyUserId(SecurityContextHolder.getUserId());
        }
        List<SeedStockOutLog> seedStockOutLogList = seedStockOutLogMapper.selectSelective(seedStockOutLog);
        PageInfo<SeedStockOutLog> srcPageInfo = new PageInfo<>(seedStockOutLogList);
        PageInfo<SeedStockOutRspDTO> pageInfo= BeanUtils.copyPageInfoProperties(srcPageInfo, SeedStockOutRspDTO.class);
        return pageInfo;
    }
}
