package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.vector.req.GetVectorTaskNumReqDTO;
import com.bio.drqi.manage.vector.req.QueryPageVectorReqDTO;
import com.bio.drqi.manage.vector.req.VectorTaskModifyVectorTaskCodeReqDTO;
import com.bio.drqi.manage.vector.rsp.CerImplementationPlanBaseInfoRspDTO;
import com.bio.drqi.manage.vector.rsp.CerImplementationPlanFullInfoRspDTO;
import com.bio.drqi.manage.vector.rsp.StepListRspDTO;
import com.bio.drqi.manage.vector.rsp.VectorListPageRspDTO;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.manage.vector.rsp.VectorTaskSpeciesRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface VectorTaskService {

    PageInfo<VectorListPageRspDTO> listPage(QueryPageVectorReqDTO queryPageVectorReqDTO);

    List<CerImplementationPlanBaseInfoRspDTO> listAll();

    List<CerImplementationPlanBaseInfoRspDTO> listBySpeciesCode(String speciesCode);

    List<CerImplementationPlanBaseInfoRspDTO> listAllBySubProject(Integer subProjectId);

    List<CerImplementationPlanBaseInfoRspDTO> listForVectorBuild(Integer subProjectId);

    List<CerImplementationPlanBaseInfoRspDTO> listForTransForm(Integer subProjectId);

    List<CerImplementationPlanBaseInfoRspDTO> listForMoveSeed();

    List<CerImplementationPlanBaseInfoRspDTO> listForFirstSample(String speciesCode);


    List<CerImplementationPlanBaseInfoRspDTO> listForPlasmid(Integer subProjectId);

    String getTaskNum(GetVectorTaskNumReqDTO getVectorTaskNumReqDTO);

    List<StepListRspDTO> stepList(Integer id);

    List<StepListRspDTO> stepListByCode(String vectorTaskCode);

    CerImplementationPlanBaseInfoRspDTO detail(Integer id);

    CerImplementationPlanBaseInfoRspDTO detailByCode(String vectorTaskCode);

    CerImplementationPlanFullInfoRspDTO fullInfo(String vectorTaskCode);

    void exportFullInfoExcel(String vectorTaskCode, HttpServletResponse response);

    void stop(Integer id);

    void start(Integer id);

    void complete(Integer id);

    String getInstantVerifyTaskCode(String vectorTaskCode);

    List<VectorTaskSpeciesRspDTO> findAllSpecies();

    void delete(Integer id);

    void modifyVectorTaskCode(VectorTaskModifyVectorTaskCodeReqDTO vectorTaskModifyVectorTaskCodeReqDTO);

}
