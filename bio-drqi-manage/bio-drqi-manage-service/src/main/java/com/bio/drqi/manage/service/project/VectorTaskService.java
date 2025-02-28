package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.vector.req.GetVectorTaskNumReqDTO;
import com.bio.drqi.manage.vector.req.QueryPageVectorReqDTO;
import com.bio.drqi.manage.vector.rsp.CerImplementationPlanBaseInfoRspDTO;
import com.bio.drqi.manage.vector.rsp.StepListRspDTO;
import com.bio.drqi.manage.vector.rsp.VectorListPageRspDTO;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.manage.vector.rsp.VectorTaskSpeciesRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface VectorTaskService {

    PageInfo<VectorListPageRspDTO> ListPage(QueryPageVectorReqDTO queryPageVectorReqDTO);

    List<CerImplementationPlanBaseInfoRspDTO> listBySubProject(Integer subProjectId);

    List<CerImplementationPlanBaseInfoRspDTO> listAll();
    List<CerImplementationPlanBaseInfoRspDTO> listApproveAll(String speciesCode);
    List<CerImplementationPlanBaseInfoRspDTO> listForTransForm();

    String getTaskNum(GetVectorTaskNumReqDTO getVectorTaskNumReqDTO);

    List<StepListRspDTO> stepList(Integer id);

    List<StepListRspDTO> stepListByCode(String vectorTaskCode);

    VectorTaskAddDTO detail(Integer id);

    void stop(Integer id);

    void start(Integer id);

    void complete(Integer id);

    String getInstantVerifyTaskCode(String vectorTaskCode);

    List<VectorTaskSpeciesRspDTO> findAllSpecies();
}
