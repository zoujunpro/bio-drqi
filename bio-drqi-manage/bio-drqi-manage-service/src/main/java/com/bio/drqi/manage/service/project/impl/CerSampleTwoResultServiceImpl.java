package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.BioSampleSampleTwoResultDetailTb;
import com.bio.drqi.domain.BioSampleSampleTwoResultTb;
import com.bio.drqi.external.client.BioInfoClientApi;
import com.bio.drqi.manage.sample.req.CerSampleTwoResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListDetailRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListPageRspDTO;
import com.bio.drqi.manage.service.common.SynSampleTestResultService;
import com.bio.drqi.manage.service.project.CerSampleTwoResultService;
import com.bio.drqi.mapper.BioSampleSampleTwoResultDetailTbMapper;
import com.bio.drqi.mapper.BioSampleSampleTwoResultTbMapper;
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
    private BioSampleSampleTwoResultTbMapper bioSampleSampleTwoResultTbMapper;

    @Resource
    private BioSampleSampleTwoResultDetailTbMapper bioSampleSampleTwoResultDetailTbMapper;

    @Resource
    private SynSampleTestResultService synSampleTestResultService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synOne(Integer id) {
        BioSampleSampleTwoResultTb bioSampleSampleTwoResultTb = bioSampleSampleTwoResultTbMapper.selectById(id);
        if (Objects.isNull(bioSampleSampleTwoResultTb)) {
            throw new BusinessException("excel没匹配到该生信检测数据");
        }
        bioSampleSampleTwoResultDetailTbMapper.deleteByApplyNoAndSampleCode(bioSampleSampleTwoResultTb.getApplyNo(), bioSampleSampleTwoResultTb.getSampleCode());
        List<BioSampleSampleTwoResultDetailTb> bioSampleSampleTwoResultDetailTbList = synSampleTestResultService.synBioResult(Arrays.asList(bioSampleSampleTwoResultTb));
        if (CollectionUtil.isNotEmpty(bioSampleSampleTwoResultDetailTbList)) {
            for (BioSampleSampleTwoResultDetailTb cerSampleTestBioInfoResultTb : bioSampleSampleTwoResultDetailTbList) {
                bioSampleSampleTwoResultDetailTbMapper.insert(cerSampleTestBioInfoResultTb);
            }
        }
        //更新结果状态
        bioSampleSampleTwoResultTbMapper.updateById(bioSampleSampleTwoResultTb);
    }
}
