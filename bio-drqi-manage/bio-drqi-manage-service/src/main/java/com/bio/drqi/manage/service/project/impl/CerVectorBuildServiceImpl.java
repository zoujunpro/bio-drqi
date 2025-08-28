package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.PageUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.CerVectorGroupTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.domain.CerVectorTb;
import com.bio.drqi.manage.service.project.CerVectorBuildService;
import com.bio.drqi.manage.vector.req.CerVectorBuildListPageReqDTO;
import com.bio.drqi.manage.vector.rsp.CerVectorBuildListPageRspDTO;
import com.bio.drqi.mapper.CerVectorGroupTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.CerVectorTbMapper;
import com.bio.drqi.manage.vector.rsp.VectorBuildDetailRspDTO;
import com.bio.common.core.util.BeanUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class CerVectorBuildServiceImpl implements CerVectorBuildService {


    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;


    @Override
    public PageInfo<CerVectorBuildListPageRspDTO> listPage(CerVectorBuildListPageReqDTO cerVectorBuildListPageReqDTO) {
        PageHelper.startPage(cerVectorBuildListPageReqDTO.getPageNum(), cerVectorBuildListPageReqDTO.getPageSize());
        CerVectorTb selectCerVectorTb = BeanUtils.copyProperties(cerVectorBuildListPageReqDTO, CerVectorTb.class);
        if (StringUtils.isNotEmpty(cerVectorBuildListPageReqDTO.getVectorTaskCode())) {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerVectorBuildListPageReqDTO.getVectorTaskCode());
            if (cerVectorTaskTb != null) {
                selectCerVectorTb.setVectorTaskId(cerVectorTaskTb.getId());
            }
        }
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectSelective(selectCerVectorTb);
        PageInfo<CerVectorTb> srcPageInfo=new PageInfo<>(cerVectorTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo,CerVectorBuildListPageRspDTO.class);
    }

    @Override
    public List<VectorBuildDetailRspDTO> detail(Integer vectorTaskId) {
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectAllByVectorTaskId(vectorTaskId);
        return BeanUtils.copyListProperties(cerVectorTbList,VectorBuildDetailRspDTO.class);
    }


}
