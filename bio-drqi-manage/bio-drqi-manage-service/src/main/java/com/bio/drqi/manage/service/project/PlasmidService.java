package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.plasmid.req.PlasmidListPageReqDTO;
import com.bio.drqi.manage.plasmid.req.QueryPagePlasmidReqDTO;
import com.bio.drqi.manage.plasmid.rsp.PlasmidListPageRspDTO;
import com.bio.drqi.manage.plasmid.rsp.QueryPagePlasmidRspDTO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface PlasmidService {


    PageInfo<PlasmidListPageRspDTO> listPage(PlasmidListPageReqDTO plasmidListPageReqDTO);

    List<QueryPagePlasmidRspDTO> listByVectorTask(QueryPagePlasmidReqDTO queryPagePlasmidReqDTO);

    void downPlasmidCheckTemplate( String vectorTaskCode, HttpServletResponse response);



}
