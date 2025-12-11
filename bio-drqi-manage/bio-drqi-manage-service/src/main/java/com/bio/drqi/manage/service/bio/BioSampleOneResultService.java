package com.bio.drqi.manage.service.bio;

import com.bio.drqi.manage.sample.req.CerSampleOneResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultListPageRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultQueryBySampleCodeRspDTO;
import com.github.pagehelper.PageInfo;

public interface BioSampleOneResultService {

    PageInfo<CerSampleOneResultListPageRspDTO> listPage(CerSampleOneResultListPageReqDTO cerSampleOneResultListPageReqDTO);

    CerSampleOneResultQueryBySampleCodeRspDTO  queryOneResultBySampleCode(String sampleCode);
}
