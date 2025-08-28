package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.transform.req.ApprovePassTransformQueryReqDTO;
import com.bio.drqi.manage.transform.req.TransformListByVectorTaskReqDTO;
import com.bio.drqi.manage.transform.req.TransformListByVectorTaskRspDTO;
import com.bio.drqi.manage.transform.req.TransformListPageReqDTO;
import com.bio.drqi.manage.transform.rsp.ApprovePassTransformQueryRspDTO;
import com.bio.drqi.manage.transform.rsp.TransformListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface TransformService {


    PageInfo<TransformListPageRspDTO> listPage(TransformListPageReqDTO transformListPageReqDTO);

    List<TransformListByVectorTaskRspDTO> listByVectorTask(TransformListByVectorTaskReqDTO transformListByVectorTaskReqDTO);

    List<ApprovePassTransformQueryRspDTO> approvePassTransformQuery(ApprovePassTransformQueryReqDTO approvePassTransformQueryReqDTO);

}
