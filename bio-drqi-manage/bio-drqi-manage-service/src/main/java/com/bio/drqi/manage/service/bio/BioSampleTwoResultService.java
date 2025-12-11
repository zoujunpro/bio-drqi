package com.bio.drqi.manage.service.bio;

import com.bio.drqi.manage.sample.req.CerSampleTwoResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListDetailRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BioSampleTwoResultService {


    PageInfo<CerSampleTwoResultListPageRspDTO> listPage(CerSampleTwoResultListPageReqDTO cerSampleTwoResultListPageReqDTO);

    List<CerSampleTwoResultListDetailRspDTO> listDetail( Integer id);

   Object detail( Integer detailId);

    void synOne(@RequestParam Integer id);


    void deleteNgsResult(String uniqueDbCode);
}
