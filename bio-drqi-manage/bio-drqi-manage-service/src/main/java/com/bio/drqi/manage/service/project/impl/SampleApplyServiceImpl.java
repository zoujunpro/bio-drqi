package com.bio.drqi.manage.service.project.impl;

import com.bio.drqi.manage.sample.req.SampleApplyListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleApplyListPageRspDTO;
import com.bio.drqi.manage.service.project.SampleApplyService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SampleApplyServiceImpl implements SampleApplyService {
    @Override
    public PageInfo<SampleApplyListPageRspDTO> listPage(SampleApplyListPageReqDTO sampleApplyListPageReqDTO) {
        return null;
    }
}
