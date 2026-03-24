package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.service.TcPollinationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TcPollinationServiceImpl implements TcPollinationService {

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Override
    public PageInfo<TcPollinationListPageDetailRspDTO> listPage(TcPollinationListPageDetailReqDTO tcPollinationListPageDetailReqDTO) {
        PageHelper.startPage(tcPollinationListPageDetailReqDTO.getPageNum(), tcPollinationListPageDetailReqDTO.getPageSize());
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectSelective(BeanUtils.copyProperties(tcPollinationListPageDetailReqDTO, TcPollinationTb.class));
        PageInfo<TcPollinationTb> srcPageInfo = new PageInfo<>(tcPollinationTbList);
        PageInfo<TcPollinationListPageDetailRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, TcPollinationListPageDetailRspDTO.class);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, String> codeNameCerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        if (CollectionUtil.isNotEmpty(resultPageInfo.getList())) {
            resultPageInfo.getList().forEach(tcPollinationListPageDetailRspDTO -> {
                tcPollinationListPageDetailRspDTO.setFBreedName(codeNameCerBreedDictMap.get(tcPollinationListPageDetailRspDTO.getFBreedCode()));
                tcPollinationListPageDetailRspDTO.setMBreedName(codeNameCerBreedDictMap.get(tcPollinationListPageDetailRspDTO.getFBreedCode()));
            });
        }
        return resultPageInfo;
    }
}
