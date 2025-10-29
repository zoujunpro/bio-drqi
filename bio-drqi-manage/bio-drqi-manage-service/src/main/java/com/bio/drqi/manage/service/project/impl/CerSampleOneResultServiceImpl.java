package com.bio.drqi.manage.service.project.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.BioSampleSampleOneResultTb;
import com.bio.drqi.manage.sample.req.CerSampleOneResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultListPageRspDTO;
import com.bio.drqi.manage.service.project.CerSampleOneResultService;
import com.bio.drqi.mapper.BioSampleSampleOneResultTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CerSampleOneResultServiceImpl implements CerSampleOneResultService {

    @Resource
    private BioSampleSampleOneResultTbMapper bioSampleSampleOneResultTbMapper;

    @Override
    public PageInfo<CerSampleOneResultListPageRspDTO> listPage(CerSampleOneResultListPageReqDTO cerSampleOneResultListPageReqDTO) {
        PageHelper.startPage(cerSampleOneResultListPageReqDTO.getPageNum(), cerSampleOneResultListPageReqDTO.getPageSize());
        List<BioSampleSampleOneResultTb> bioSampleSampleOneResultTbList = bioSampleSampleOneResultTbMapper.selectSelective(BeanUtils.copyProperties(cerSampleOneResultListPageReqDTO, BioSampleSampleOneResultTb.class));
        PageInfo<BioSampleSampleOneResultTb> srcPageInfo=new PageInfo<>(bioSampleSampleOneResultTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, CerSampleOneResultListPageRspDTO.class);
    }
}
