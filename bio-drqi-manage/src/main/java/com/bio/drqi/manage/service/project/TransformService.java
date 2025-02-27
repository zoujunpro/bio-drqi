package com.bio.drqi.manage.service.project;

import com.bio.drqi.transform.req.ApprovePassTransformQueryReqDTO;
import com.bio.drqi.transform.req.TransformListByVectorTaskReqDTO;
import com.bio.drqi.transform.req.TransformListByVectorTaskRspDTO;
import com.bio.drqi.transform.rsp.ApprovePassTransformQueryRspDTO;

import java.util.List;

public interface TransformService {

    List<TransformListByVectorTaskRspDTO> listByVectorTask(TransformListByVectorTaskReqDTO transformListByVectorTaskReqDTO);

    List<ApprovePassTransformQueryRspDTO> approvePassTransformQuery(ApprovePassTransformQueryReqDTO approvePassTransformQueryReqDTO);

}
