package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.BioSampleTestTwoResultDetailTb;
import com.bio.drqi.domain.BioSampleTestTwoResultTb;
import com.bio.drqi.external.client.BioInfoClientApi;
import com.bio.drqi.manage.sample.req.CerSampleTwoResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListDetailRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListPageRspDTO;
import com.bio.drqi.manage.service.common.SynSampleTestResultService;
import com.bio.drqi.manage.service.project.CerSampleTwoResultService;
import com.bio.drqi.mapper.BioSampleTestTwoResultDetailTbMapper;
import com.bio.drqi.mapper.BioSampleTestTwoResultTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class CerSampleTwoResultServiceImpl implements CerSampleTwoResultService {


    @Resource
    private BioInfoClientApi bioInfoClientApi;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;

    @Resource
    private SynSampleTestResultService synSampleTestResultService;

    @Override
    public PageInfo<CerSampleTwoResultListPageRspDTO> listPage(CerSampleTwoResultListPageReqDTO cerSampleTwoResultListPageReqDTO) {
        PageHelper.startPage(cerSampleTwoResultListPageReqDTO.getPageNum(), cerSampleTwoResultListPageReqDTO.getPageSize());
        List<BioSampleTestTwoResultTb> bioSampleTestTwoResultTbList = bioSampleTestTwoResultTbMapper.selectSelective(BeanUtils.copyProperties(cerSampleTwoResultListPageReqDTO, BioSampleTestTwoResultTb.class));
        PageInfo<BioSampleTestTwoResultTb> srcPageInfo = new PageInfo<>(bioSampleTestTwoResultTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, CerSampleTwoResultListPageRspDTO.class);
    }

    @Override
    public List<CerSampleTwoResultListDetailRspDTO> listDetail(Integer id) {
        BioSampleTestTwoResultTb bioSampleTestTwoResultTb = bioSampleTestTwoResultTbMapper.selectById(id);
        List<BioSampleTestTwoResultDetailTb> bioSampleTestTwoResultDetailTbList = bioSampleTestTwoResultDetailTbMapper.selectAllByApplyNoAndSampleCode(bioSampleTestTwoResultTb.getApplyNo(), bioSampleTestTwoResultTb.getSampleCode());
        return BeanUtils.copyListProperties(bioSampleTestTwoResultDetailTbList, CerSampleTwoResultListDetailRspDTO.class);
    }

    @Override
    public Object detail(Integer detailId) {
        BioSampleTestTwoResultDetailTb bioSampleTestTwoResultDetailTb = bioSampleTestTwoResultDetailTbMapper.selectById(detailId);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sampleID", bioSampleTestTwoResultDetailTb.getSampleId());
        paramMap.put("QBuniqCode", bioSampleTestTwoResultDetailTb.getUniqueDbCode());
        paramMap.put("HapID", bioSampleTestTwoResultDetailTb.getHapId());
        Object o = bioInfoClientApi.sampleTestBioInfoResultDetail(paramMap);
        return o;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synOne(Integer id) {
        BioSampleTestTwoResultTb bioSampleTestTwoResultTb = bioSampleTestTwoResultTbMapper.selectById(id);
        if (Objects.isNull(bioSampleTestTwoResultTb)) {
            throw new BusinessException("excel没匹配到该生信检测数据");
        }
        List<BioSampleTestTwoResultDetailTb> bioSampleTestTwoResultDetailTbList = synSampleTestResultService.synBioResult(Arrays.asList(bioSampleTestTwoResultTb));
        if (CollectionUtil.isNotEmpty(bioSampleTestTwoResultDetailTbList)) {
            for (BioSampleTestTwoResultDetailTb bioSampleTestTwoResultDetailTb : bioSampleTestTwoResultDetailTbList) {
                bioSampleTestTwoResultDetailTbMapper.deleteByApplyNoAndSampleCodeAndUniqueDbCode(bioSampleTestTwoResultDetailTb.getApplyNo(), bioSampleTestTwoResultDetailTb.getSampleCode(),bioSampleTestTwoResultDetailTb.getUniqueDbCode());
                bioSampleTestTwoResultDetailTbMapper.insert(bioSampleTestTwoResultDetailTb);
            }
        }
        //更新结果状态
        bioSampleTestTwoResultTbMapper.updateById(bioSampleTestTwoResultTb);
    }
}
