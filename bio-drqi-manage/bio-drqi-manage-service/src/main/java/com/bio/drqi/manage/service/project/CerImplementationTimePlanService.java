package com.bio.drqi.manage.service.project;


import com.bio.drqi.manage.timePlan.VectorTaskTimePlanAddReqDTO;
import com.bio.drqi.manage.timePlan.VectorTaskTimePlanExportReqDTO;
import com.bio.drqi.manage.timePlan.VectorTaskTimePlanListRspDTO;

import javax.servlet.http.HttpServletResponse;

public interface CerImplementationTimePlanService {

    VectorTaskTimePlanListRspDTO list(String vectorTaskCode);

    void add( VectorTaskTimePlanAddReqDTO vectorTaskTimePlanAddReqDTO);

    void exportExcel(VectorTaskTimePlanExportReqDTO vectorTaskTimePlanExportReqDTO, HttpServletResponse httpServletResponse);
}
