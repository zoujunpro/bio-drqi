package com.bio.drqi.tc.service.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.TcExperimentTb;
import com.bio.drqi.mapper.TcExperimentTbMapper;
import com.bio.drqi.tc.req.TcExperimentApplyListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentApplyListPageRspDTO;
import com.bio.drqi.tc.service.TcExperimentApplyService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TcExperimentApplyServiceImpl implements TcExperimentApplyService {

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Override
    public PageInfo<TcExperimentApplyListPageRspDTO> listPage(TcExperimentApplyListPageReqDTO tcExperimentApplyListPageReqDTO) {
        PageHelper.startPage(tcExperimentApplyListPageReqDTO.getPageNum(), tcExperimentApplyListPageReqDTO.getPageSize());
        TcExperimentTb tcExperimentTb = new TcExperimentTb();
        tcExperimentTb.setVectorTaskCodes(tcExperimentApplyListPageReqDTO.getVectorTaskCode());
        tcExperimentTb.setProjectCodes(tcExperimentApplyListPageReqDTO.getProjectCode());
        tcExperimentTb.setSpeciesCode(tcExperimentApplyListPageReqDTO.getSpeciesCode());
        tcExperimentApplyListPageReqDTO.setExperimentCode(tcExperimentApplyListPageReqDTO.getExperimentCode());
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectSelective(tcExperimentTb);
        PageInfo<TcExperimentTb> srcPageInfo=new PageInfo<>(tcExperimentTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo,TcExperimentApplyListPageRspDTO.class);
    }
}
