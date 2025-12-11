package com.bio.drqi.manage.service.bio.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.BioSampleTestOneResultTb;
import com.bio.drqi.domain.BioSampleTestTb;
import com.bio.drqi.manage.sample.req.CerSampleOneResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultListPageRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultQueryBySampleCodeRspDTO;
import com.bio.drqi.manage.service.bio.BioSampleOneResultService;
import com.bio.drqi.mapper.BioSampleTestOneResultTbMapper;
import com.bio.drqi.mapper.BioSampleTestTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BioSampleOneResultServiceImpl implements BioSampleOneResultService {

    @Resource
    private BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Override
    public PageInfo<CerSampleOneResultListPageRspDTO> listPage(CerSampleOneResultListPageReqDTO cerSampleOneResultListPageReqDTO) {
        PageHelper.startPage(cerSampleOneResultListPageReqDTO.getPageNum(), cerSampleOneResultListPageReqDTO.getPageSize());
        List<BioSampleTestOneResultTb> bioSampleSampleOneResultTbList = bioSampleTestOneResultTbMapper.selectSelective(BeanUtils.copyProperties(cerSampleOneResultListPageReqDTO, BioSampleTestOneResultTb.class));
        PageInfo<BioSampleTestOneResultTb> srcPageInfo = new PageInfo<>(bioSampleSampleOneResultTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, CerSampleOneResultListPageRspDTO.class);
    }

    @Override
    public CerSampleOneResultQueryBySampleCodeRspDTO queryOneResultBySampleCode(String sampleCode) {
        List<BioSampleTestOneResultTb> bioSampleTestOneResultTbList = bioSampleTestOneResultTbMapper.selectAllBySampleCodeOrderByIdDesc(sampleCode);
        if (CollectionUtil.isNotEmpty(bioSampleTestOneResultTbList)) {
            return BeanUtils.copyProperties(bioSampleTestOneResultTbList.get(0), CerSampleOneResultQueryBySampleCodeRspDTO.class);
        } else {
            List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(sampleCode);
            if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
                return BeanUtils.copyProperties(bioSampleTestTbList.get(0), CerSampleOneResultQueryBySampleCodeRspDTO.class);
            }
        }
        return null;

    }
}
