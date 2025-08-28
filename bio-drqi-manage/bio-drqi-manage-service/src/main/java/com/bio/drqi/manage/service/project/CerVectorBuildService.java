package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.vector.req.CerVectorBuildListPageReqDTO;
import com.bio.drqi.manage.vector.rsp.CerVectorBuildListPageRspDTO;
import com.bio.drqi.manage.vector.rsp.VectorBuildDetailRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CerVectorBuildService {

    List<VectorBuildDetailRspDTO> detail(Integer vectorTaskId);


    PageInfo<CerVectorBuildListPageRspDTO>   listPage(CerVectorBuildListPageReqDTO cerVectorBuildListPageReqDTO);
}
