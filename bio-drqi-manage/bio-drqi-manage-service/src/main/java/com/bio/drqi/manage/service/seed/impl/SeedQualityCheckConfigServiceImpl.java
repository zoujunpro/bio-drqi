package com.bio.drqi.manage.service.seed.impl;


import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.common.dto.PageDTO;
import com.bio.drqi.domain.SeedQualityCheckConfig;
import com.bio.drqi.manage.seed.SeedQualityCheckAddReqDTO;
import com.bio.drqi.manage.seed.SeedQualityCheckEditReqDTO;
import com.bio.drqi.manage.seed.SeedQualityCheckRspDTO;
import com.bio.drqi.manage.service.seed.SeedQualityCheckConfigService;
import com.bio.drqi.mapper.SeedQualityCheckConfigMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SeedQualityCheckConfigServiceImpl implements SeedQualityCheckConfigService {

    @Resource
    private SeedQualityCheckConfigMapper seedQualityCheckConfigMapper;


    @Override
    public PageInfo<SeedQualityCheckRspDTO> listPage(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<SeedQualityCheckConfig> seedQualityCheckConfigList = pageDTO.getId() == null
                ? seedQualityCheckConfigMapper.selectAllOrderByIdDesc()
                : singletonList(seedQualityCheckConfigMapper.selectById(pageDTO.getId()));
        PageInfo<SeedQualityCheckConfig> srcPageInfo = new PageInfo<>(seedQualityCheckConfigList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, SeedQualityCheckRspDTO.class);
    }

    private <T> List<T> singletonList(T item) {
        return item == null ? Collections.emptyList() : Collections.singletonList(item);
    }

    @Override
    public void add(SeedQualityCheckAddReqDTO seedQualityCheckAddReqDTO) {
        SeedQualityCheckConfig seedQualityCheckConfig = new SeedQualityCheckConfig();
        seedQualityCheckConfig.setFieldName(seedQualityCheckAddReqDTO.getFieldName());
        seedQualityCheckConfig.setFieldCode(seedQualityCheckAddReqDTO.getFieldCode());
        seedQualityCheckConfigMapper.insert(seedQualityCheckConfig);

    }

    @Override
    public void edit(SeedQualityCheckEditReqDTO seedQualityCheckEditReqDTO) {
        SeedQualityCheckConfig seedQualityCheckConfig = seedQualityCheckConfigMapper.selectById(seedQualityCheckEditReqDTO.getId());
        seedQualityCheckConfig.setFieldName(seedQualityCheckEditReqDTO.getFieldName());
        seedQualityCheckConfig.setFieldCode(seedQualityCheckEditReqDTO.getFieldCode());
        seedQualityCheckConfigMapper.updateById(seedQualityCheckConfig);
    }

    @Override
    public void delete(Integer id) {
        seedQualityCheckConfigMapper.deleteById(id);
    }

}
