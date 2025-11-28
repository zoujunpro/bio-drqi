package com.bio.drqi.manage.service.bio;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerSampleApplyTb;
import com.bio.drqi.manage.bio.req.BioSampleApplyListPageReqDTO;
import com.bio.drqi.manage.bio.rsp.BioSampleApplyListPageRspDTO;
import com.bio.drqi.manage.sample.req.SampleApplyListPageReqDTO;
import com.bio.drqi.manage.sample.req.SampleTestByVectorTaskReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleApplyListPageRspDTO;
import com.bio.drqi.manage.sample.rsp.SampleApplyRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BioSampleApplyService {

    PageInfo<BioSampleApplyListPageRspDTO> listPage(BioSampleApplyListPageReqDTO bioSampleApplyListPageReqDTO);


    List<SampleApplyRspDTO> listByVectorTask(SampleTestByVectorTaskReqDTO sampleTestByVectorTaskReqDTO);

}
