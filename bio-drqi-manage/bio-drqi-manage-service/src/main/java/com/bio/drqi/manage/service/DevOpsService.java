package com.bio.drqi.manage.service;

import com.bio.drqi.manage.devOps.DevOpsModifyProjectCodeReqDTO;
import com.bio.drqi.manage.devOps.DevOpsModifySubProjectCodeReqDTO;
import com.bio.drqi.manage.devOps.DevOpsModifyVectorTaskCodeBreedCodeReqDTO;
import com.bio.drqi.manage.devOps.DevOpsModifyVectorTaskCodeReqDTO;
import org.springframework.web.bind.annotation.RequestParam;

public interface DevOpsService {

    void modifyVectorTaskCodeBreedCode(DevOpsModifyVectorTaskCodeBreedCodeReqDTO devOpsModifyVectorTaskCodeBreedCodeReqDTO);

    void cleanSubProjectCode(DevOpsModifySubProjectCodeReqDTO devOpsModifySubProjectCodeReqDTO);

    void cleanVectorTaskCode(DevOpsModifyVectorTaskCodeReqDTO devOpsModifySubProjectCodeReqDTO);

    void cleanProjectCode(DevOpsModifyProjectCodeReqDTO devOpsModifyProjectCodeReqDTO);

    void deleteByProjectCode(String projectCode);

    void deleteByVectorTaskCode(String vectorTaskCode);
}
