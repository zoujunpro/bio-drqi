package com.bio.drqi.manage.service.bio;

import com.bio.drqi.manage.bio.req.BioSampleApplyListPageReqDTO;
import com.bio.drqi.manage.bio.rsp.BioSampleApplyListPageRspDTO;
import com.bio.drqi.manage.sample.req.SampleApplyListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleApplyListPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface BioSampleApplyService {

    PageInfo<BioSampleApplyListPageRspDTO> listPage(BioSampleApplyListPageReqDTO bioSampleApplyListPageReqDTO);

}
