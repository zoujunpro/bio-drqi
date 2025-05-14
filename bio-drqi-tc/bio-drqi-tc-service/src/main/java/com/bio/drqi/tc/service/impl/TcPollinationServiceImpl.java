package com.bio.drqi.tc.service.impl;

import com.bio.drqi.domain.TcPollinationApplyTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.mapper.TcPollinationApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageRspDTO;
import com.bio.drqi.tc.service.TcPollinationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TcPollinationServiceImpl implements TcPollinationService {

    @Resource
    private TcPollinationApplyTbMapper tcPollinationApplyTbMapper;


    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Override
    public PageInfo<TcPollinationListPageRspDTO> listPage(TcPollinationListPageReqDTO tcPollinationListPageReqDTO) {
        PageHelper.startPage(tcPollinationListPageReqDTO.getPageNum(),tcPollinationListPageReqDTO.getPageSize());

        return null;
    }

    @Override
    public void createPollinationExcel(TcPollinationCreatePollinationExcelReqDTO tcPollinationCreatePollinationExcelReqDTO) {

    }
}
