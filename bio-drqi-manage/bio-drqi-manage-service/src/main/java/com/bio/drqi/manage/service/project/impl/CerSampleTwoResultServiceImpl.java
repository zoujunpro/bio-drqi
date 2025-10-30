package com.bio.drqi.manage.service.project.impl;

import com.bio.drqi.manage.sample.req.CerSampleTwoResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListPageRspDTO;
import com.bio.drqi.manage.service.project.CerSampleTwoResultService;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

@Service
public class CerSampleTwoResultServiceImpl implements CerSampleTwoResultService {
    @Override
    public PageInfo<CerSampleTwoResultListPageRspDTO> listPage(CerSampleTwoResultListPageReqDTO cerSampleTwoResultListPageReqDTO) {
        return null;
    }
}
