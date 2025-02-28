package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.plasmid.req.QueryPagePlasmidReqDTO;
import com.bio.drqi.manage.plasmid.rsp.QueryPagePlasmidRspDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface PlasmidService {

    List<QueryPagePlasmidRspDTO> listByVectorTask(QueryPagePlasmidReqDTO queryPagePlasmidReqDTO);

    void downPlasmidCheckTemplate( String vectorTaskCode, HttpServletResponse response);



}
