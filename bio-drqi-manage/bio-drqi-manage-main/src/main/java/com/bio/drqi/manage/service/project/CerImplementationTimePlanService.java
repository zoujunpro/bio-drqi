package com.bio.drqi.manage.service.project;


import com.bio.drqi.timePlan.VectorTaskTimePlanAddReqDTO;
import com.bio.drqi.timePlan.VectorTaskTimePlanListRspDTO;

public interface CerImplementationTimePlanService {

    VectorTaskTimePlanListRspDTO list(String vectorTaskCode);

    void add( VectorTaskTimePlanAddReqDTO vectorTaskTimePlanAddReqDTO);
}
