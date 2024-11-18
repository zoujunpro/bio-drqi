package com.bio.drqi.manage.service.project;


import com.bio.cer.timePlan.VectorTaskTimePlanAddReqDTO;
import com.bio.cer.timePlan.VectorTaskTimePlanListRspDTO;

public interface CerImplementationTimePlanService {

    VectorTaskTimePlanListRspDTO list(String vectorTaskCode);

    void add( VectorTaskTimePlanAddReqDTO vectorTaskTimePlanAddReqDTO);
}
