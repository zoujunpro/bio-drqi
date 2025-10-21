package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.sample.req.SampleApplyListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleApplyListPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface SampleApplyService {

    PageInfo<SampleApplyListPageRspDTO> listPage( SampleApplyListPageReqDTO sampleApplyListPageReqDTO);

}
