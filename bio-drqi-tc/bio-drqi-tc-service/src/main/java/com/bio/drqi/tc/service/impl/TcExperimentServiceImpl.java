package com.bio.drqi.tc.service.impl;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcExperimentTb;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.mapper.TcExperimentTbMapper;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentListDetailRspDTO;
import com.bio.drqi.tc.rsp.TcExperimentListNoPollinationRspDTO;
import com.bio.drqi.tc.rsp.TcExperimentListPageRspDTO;
import com.bio.drqi.tc.service.TcExperimentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Service
@Slf4j
public class TcExperimentServiceImpl implements TcExperimentService {

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;


    @Override
    public PageInfo<TcExperimentListPageRspDTO> listPage(TcExperimentListPageReqDTO tcExperimentListPageReqDTO) {
        PageHelper.startPage(tcExperimentListPageReqDTO.getPageNum(), tcExperimentListPageReqDTO.getPageSize());
        TcExperimentTb tcExperimentTb = new TcExperimentTb();
        tcExperimentTb.setVectorTaskCodes(tcExperimentListPageReqDTO.getVectorTaskCode());
        tcExperimentTb.setProjectCodes(tcExperimentListPageReqDTO.getProjectCode());
        tcExperimentTb.setSpeciesCode(tcExperimentListPageReqDTO.getSpeciesCode());
        tcExperimentListPageReqDTO.setExperimentCode(tcExperimentListPageReqDTO.getExperimentCode());
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectSelective(tcExperimentTb);
        PageInfo<TcExperimentTb> srcPageInfo = new PageInfo<>(tcExperimentTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, TcExperimentListPageRspDTO.class);
    }


    @Override
    public List<TcExperimentListDetailRspDTO> listDetail(String experimentNum) {
        List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByExperimentNum(experimentNum);
        return BeanUtils.copyListProperties(tcExperimentDesignTbList, TcExperimentListDetailRspDTO.class);
    }

    @Override
    public List<TcExperimentListNoPollinationRspDTO> listByNoPollination() {
        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectAllByPollinationNumIsNullOrderByIdDesc();
        return BeanUtils.copyListProperties(tcExperimentTbList, TcExperimentListNoPollinationRspDTO.class);
    }
}
