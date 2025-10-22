package com.bio.drqi.manage.service.project.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerSampleApplyTb;
import com.bio.drqi.manage.sample.req.SampleApplyListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleApplyListPageRspDTO;
import com.bio.drqi.manage.service.project.SampleApplyService;
import com.bio.drqi.mapper.CerSampleApplyTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SampleApplyServiceImpl implements SampleApplyService {

    @Resource
    private CerSampleApplyTbMapper cerSampleApplyTbMapper;

    @Override
    public PageInfo<SampleApplyListPageRspDTO> listPage(SampleApplyListPageReqDTO sampleApplyListPageReqDTO) {
        PageHelper.startPage(sampleApplyListPageReqDTO.getPageNum(), sampleApplyListPageReqDTO.getPageSize());
        CerSampleApplyTb cerSampleApplyTb = BeanUtils.copyProperties(sampleApplyListPageReqDTO, CerSampleApplyTb.class);
        cerSampleApplyTb.setVectorTaskCodes(sampleApplyListPageReqDTO.getVectorTaskCode());
        List<CerSampleApplyTb> cerSampleApplyTbList = cerSampleApplyTbMapper.selectSelective(cerSampleApplyTb);
        PageInfo<CerSampleApplyTb> srcPageInfo=new PageInfo<>(cerSampleApplyTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, SampleApplyListPageRspDTO.class);
    }
}
