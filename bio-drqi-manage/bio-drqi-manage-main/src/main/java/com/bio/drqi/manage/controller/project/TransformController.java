package com.bio.drqi.manage.controller.project;


import com.bio.cer.transform.req.ApprovePassTransformQueryReqDTO;
import com.bio.cer.transform.req.TransformListByVectorTaskReqDTO;
import com.bio.cer.transform.req.TransformListByVectorTaskRspDTO;
import com.bio.cer.transform.rsp.ApprovePassTransformQueryRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.TransformService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * 取样转化
 */
@RestController
@RequestMapping("/transform")
public class TransformController {

    @Resource
    private TransformService transformService;


    /**
     * 实施方案维度查询转化信息
     */
    @PostMapping("/listByVectorTask")
    @WebLog(desc = "实施方案维度查询转化信息")
    public ResponseResult<List<TransformListByVectorTaskRspDTO>> listByVectorTask(@Validated @RequestBody TransformListByVectorTaskReqDTO transformListByVectorTaskReqDTO) {
        List<TransformListByVectorTaskRspDTO> transformListByVectorTaskRspDTOList = transformService.listByVectorTask(transformListByVectorTaskReqDTO);
        return ResponseResult.getSuccess(transformListByVectorTaskRspDTOList);
    }

    /**
     * 审批通过转化列表查询
     */
    @PostMapping("/approvePassTransformQuery")
    @WebLog(desc = "审批通过转化列表查询")
    public ResponseResult<List<ApprovePassTransformQueryRspDTO>> approvePassTransformQuery(@RequestBody @Validated ApprovePassTransformQueryReqDTO approvePassTransformQueryReqDTO) {
        List<ApprovePassTransformQueryRspDTO> list = transformService.approvePassTransformQuery(approvePassTransformQueryReqDTO);
        return ResponseResult.getSuccess(list);

    }


}
