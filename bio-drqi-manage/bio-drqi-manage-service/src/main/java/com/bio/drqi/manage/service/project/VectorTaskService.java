package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.vector.req.GetVectorTaskNumReqDTO;
import com.bio.drqi.manage.vector.req.QueryPageVectorReqDTO;
import com.bio.drqi.manage.vector.rsp.CerImplementationPlanBaseInfoRspDTO;
import com.bio.drqi.manage.vector.rsp.StepListRspDTO;
import com.bio.drqi.manage.vector.rsp.VectorListPageRspDTO;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.manage.vector.rsp.VectorTaskSpeciesRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface VectorTaskService {

    PageInfo<VectorListPageRspDTO> ListPage(QueryPageVectorReqDTO queryPageVectorReqDTO);

    List<CerImplementationPlanBaseInfoRspDTO> listAllBySubProject(Integer subProjectId);

    List<CerImplementationPlanBaseInfoRspDTO> listForVectorBuild(Integer subProjectId);

    List<CerImplementationPlanBaseInfoRspDTO> listForTransForm(Integer subProjectId);

    List<CerImplementationPlanBaseInfoRspDTO> listForMoveSeed();

    List<CerImplementationPlanBaseInfoRspDTO> listForPlasmid(Integer subProjectId);

    String getTaskNum(GetVectorTaskNumReqDTO getVectorTaskNumReqDTO);

    List<StepListRspDTO> stepList(Integer id);

    List<StepListRspDTO> stepListByCode(String vectorTaskCode);

    CerImplementationPlanBaseInfoRspDTO detail(Integer id);

    CerImplementationPlanBaseInfoRspDTO detailByCode(String vectorTaskCode);

    void stop(Integer id);

    void start(Integer id);

    void complete(Integer id);

    String getInstantVerifyTaskCode(String vectorTaskCode);

    List<VectorTaskSpeciesRspDTO> findAllSpecies();

}
