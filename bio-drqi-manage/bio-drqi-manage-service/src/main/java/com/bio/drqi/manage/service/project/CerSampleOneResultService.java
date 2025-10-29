package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.sample.req.CerSampleOneResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultListPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface CerSampleOneResultService {

    PageInfo<CerSampleOneResultListPageRspDTO> listPage(CerSampleOneResultListPageReqDTO cerSampleOneResultListPageReqDTO);
}
