package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.project.req.SubProjectListPageReqDTO;
import com.bio.drqi.manage.project.rsp.ProjectSpeciesLispRspDTO;
import com.bio.drqi.manage.project.rsp.SubProjectListPageRspDTO;
import com.bio.drqi.manage.project.rsp.SubProjectRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface SubProjectService {


    PageInfo<SubProjectListPageRspDTO> listPage(SubProjectListPageReqDTO subProjectListPageReqDTO);

    List<SubProjectRspDTO> list(Integer projectId);


    List<ProjectSpeciesLispRspDTO> findSubProjectAllSpecies(String subProjectCode);

}
