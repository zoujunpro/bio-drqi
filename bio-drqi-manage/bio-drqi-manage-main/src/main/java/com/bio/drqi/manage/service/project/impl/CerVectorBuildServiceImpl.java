package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.cer.domain.CerVectorGroupTb;
import com.bio.cer.domain.CerVectorTb;
import com.bio.cer.mapper.CerVectorGroupTbMapper;
import com.bio.cer.mapper.CerVectorTbMapper;
import com.bio.cer.service.project.CerVectorBuildService;
import com.bio.cer.vector.rsp.VectorBuildDetailRspDTO;
import com.bio.common.core.util.BeanUtils;
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
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;

    @Override
    public VectorBuildDetailRspDTO detail(Integer vectorTaskId) {
        VectorBuildDetailRspDTO vectorBuildDetailRspDTO = new VectorBuildDetailRspDTO();
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectAllByVectorTaskId(vectorTaskId);
        List<CerVectorGroupTb> cerVectorGroupTbList = cerVectorGroupTbMapper.selectAllByVectorTaskId(vectorTaskId);
        if (CollectionUtil.isNotEmpty(cerVectorGroupTbList)) {
            List<VectorBuildDetailRspDTO.CerVectorGroup> cerVectorGroupList = BeanUtils.copyToList(cerVectorGroupTbList, VectorBuildDetailRspDTO.CerVectorGroup.class);
            vectorBuildDetailRspDTO.setCerVectorGroupList(cerVectorGroupList);
        }
        if (CollectionUtil.isNotEmpty(cerVectorTbList)) {
            List<VectorBuildDetailRspDTO.CerVector> cerVectorList = BeanUtils.copyToList(cerVectorTbList, VectorBuildDetailRspDTO.CerVector.class);
            vectorBuildDetailRspDTO.setCerVectorList(cerVectorList);
        }
        return vectorBuildDetailRspDTO;
    }
}
