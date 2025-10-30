package com.bio.drqi.manage.service.project.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.BioSampleSampleTwoResultDetailTb;
import com.bio.drqi.domain.BioSampleSampleTwoResultTb;
import com.bio.drqi.external.client.BioInfoClientApi;
import com.bio.drqi.manage.sample.req.CerSampleTwoResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListDetailRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListPageRspDTO;
import com.bio.drqi.manage.service.project.CerSampleTwoResultService;
import com.bio.drqi.mapper.BioSampleSampleTwoResultDetailTbMapper;
import com.bio.drqi.mapper.BioSampleSampleTwoResultTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CerSampleTwoResultServiceImpl implements CerSampleTwoResultService {



    @Resource
    private BioInfoClientApi bioInfoClientApi;

    @Resource
    private BioSampleSampleTwoResultTbMapper bioSampleSampleTwoResultTbMapper;

    @Resource
    private BioSampleSampleTwoResultDetailTbMapper bioSampleSampleTwoResultDetailTbMapper;

    @Override
    public PageInfo<CerSampleTwoResultListPageRspDTO> listPage(CerSampleTwoResultListPageReqDTO cerSampleTwoResultListPageReqDTO) {
        PageHelper.startPage(cerSampleTwoResultListPageReqDTO.getPageNum(), cerSampleTwoResultListPageReqDTO.getPageSize());
        List<BioSampleSampleTwoResultTb> bioSampleSampleTwoResultTbList = bioSampleSampleTwoResultTbMapper.selectSelective(BeanUtils.copyProperties(cerSampleTwoResultListPageReqDTO, BioSampleSampleTwoResultTb.class));
        PageInfo<BioSampleSampleTwoResultTb> srcPageInfo = new PageInfo<>(bioSampleSampleTwoResultTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, CerSampleTwoResultListPageRspDTO.class);
    }

    @Override
    public List<CerSampleTwoResultListDetailRspDTO> listDetail(Integer id) {
        BioSampleSampleTwoResultTb bioSampleSampleTwoResultTb = bioSampleSampleTwoResultTbMapper.selectById(id);
        List<BioSampleSampleTwoResultDetailTb> bioSampleSampleTwoResultDetailTbList = bioSampleSampleTwoResultDetailTbMapper.selectAllByApplyNoAndSampleCode(bioSampleSampleTwoResultTb.getApplyNo(), bioSampleSampleTwoResultTb.getSampleCode());
        return BeanUtils.copyListProperties(bioSampleSampleTwoResultDetailTbList, CerSampleTwoResultListDetailRspDTO.class);
    }

    @Override
    public Object detail(Integer detailId) {
        BioSampleSampleTwoResultDetailTb bioSampleSampleTwoResultDetailTb = bioSampleSampleTwoResultDetailTbMapper.selectById(detailId);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sampleID", bioSampleSampleTwoResultDetailTb.getSampleId());
        paramMap.put("QBuniqCode", bioSampleSampleTwoResultDetailTb.getUniqueDbCode());
        paramMap.put("HapID", bioSampleSampleTwoResultDetailTb.getHapId());
        Object o = bioInfoClientApi.sampleTestBioInfoResultDetail(paramMap);
        return o;
    }
}
