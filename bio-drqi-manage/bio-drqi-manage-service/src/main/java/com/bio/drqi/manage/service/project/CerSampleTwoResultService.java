package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.sample.req.CerSampleOneResultListPageReqDTO;
import com.bio.drqi.manage.sample.req.CerSampleTwoResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultListPageRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListDetailRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CerSampleTwoResultService {


    PageInfo<CerSampleTwoResultListPageRspDTO> listPage(CerSampleTwoResultListPageReqDTO cerSampleTwoResultListPageReqDTO);

    List<CerSampleTwoResultListDetailRspDTO> listDetail( Integer id);

   Object detail( Integer detailId);

}
