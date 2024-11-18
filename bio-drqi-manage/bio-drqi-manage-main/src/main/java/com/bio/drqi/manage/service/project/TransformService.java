package com.bio.drqi.manage.service.project;

import com.bio.cer.transform.req.ApprovePassTransformQueryReqDTO;
import com.bio.cer.transform.req.TransformListByVectorTaskReqDTO;
import com.bio.cer.transform.req.TransformListByVectorTaskRspDTO;
import com.bio.cer.transform.rsp.ApprovePassTransformQueryRspDTO;

import java.util.List;

public interface TransformService {

    List<TransformListByVectorTaskRspDTO> listByVectorTask(TransformListByVectorTaskReqDTO transformListByVectorTaskReqDTO);

    List<ApprovePassTransformQueryRspDTO> approvePassTransformQuery(ApprovePassTransformQueryReqDTO approvePassTransformQueryReqDTO);

}
