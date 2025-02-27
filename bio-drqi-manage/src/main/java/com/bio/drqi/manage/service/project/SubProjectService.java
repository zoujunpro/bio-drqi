package com.bio.drqi.manage.service.project;

import com.bio.drqi.project.rsp.ProjectSpeciesLispRspDTO;
import com.bio.drqi.project.rsp.SubProjectRspDTO;

import java.util.List;

public interface SubProjectService {

    List<SubProjectRspDTO> list(Integer projectId);


    List<ProjectSpeciesLispRspDTO> findSubProjectAllSpecies(String subProjectCode);

}
