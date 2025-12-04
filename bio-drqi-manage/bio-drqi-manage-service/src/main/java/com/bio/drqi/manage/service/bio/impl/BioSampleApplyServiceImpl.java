package com.bio.drqi.manage.service.bio.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.domain.BioSampleApplyTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.manage.bio.req.BioSampleApplyListPageReqDTO;
import com.bio.drqi.manage.bio.rsp.BioSampleApplyListPageRspDTO;
import com.bio.drqi.manage.sample.req.SampleTestByVectorTaskReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleApplyRspDTO;
import com.bio.drqi.manage.service.bio.BioSampleApplyService;
import com.bio.drqi.mapper.BioSampleApplyTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class BioSampleApplyServiceImpl implements BioSampleApplyService {

    @Resource
    private BioSampleApplyTbMapper bioSampleApplyTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Override
    public PageInfo<BioSampleApplyListPageRspDTO> listPage(BioSampleApplyListPageReqDTO bioSampleApplyListPageReqDTO) {
        PageHelper.startPage(bioSampleApplyListPageReqDTO.getPageNum(), bioSampleApplyListPageReqDTO.getPageSize());
        BioSampleApplyTb bioSampleApplyTb = BeanUtils.copyProperties(bioSampleApplyListPageReqDTO, BioSampleApplyTb.class);
        bioSampleApplyTb.setVectorTaskCodes(bioSampleApplyListPageReqDTO.getVectorTaskCode());
        List<BioSampleApplyTb> bioSampleApplyTbList = bioSampleApplyTbMapper.selectSelective(bioSampleApplyTb);
        PageInfo<BioSampleApplyTb> srcPageInfo = new PageInfo<>(bioSampleApplyTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BioSampleApplyListPageRspDTO.class);
    }

    @Override
    public List<SampleApplyRspDTO> listByVectorTask(SampleTestByVectorTaskReqDTO sampleTestByVectorTaskReqDTO) {
        CerVectorTaskTb cerVectorTaskTb  = cerVectorTaskTbMapper.selectById(sampleTestByVectorTaskReqDTO.getVectorTaskId());
        List<BioSampleApplyTb> bioSampleApplyTbList = bioSampleApplyTbMapper.selectAllByVectorTaskCodeAndSourceCode(cerVectorTaskTb.getVectorTaskCode(), SourceCodeEnum.project.name());
        return BeanUtils.copyListProperties(bioSampleApplyTbList, SampleApplyRspDTO.class);
    }
}
