package com.bio.drqi.manage.controller.project;


import com.bio.drqi.manage.transform.req.ApprovePassTransformQueryReqDTO;
import com.bio.drqi.manage.transform.req.TransformListByVectorTaskReqDTO;
import com.bio.drqi.manage.transform.req.TransformListByVectorTaskRspDTO;
import com.bio.drqi.manage.transform.req.TransformListPageReqDTO;
import com.bio.drqi.manage.transform.rsp.ApprovePassTransformQueryRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.project.TransformService;
import com.bio.drqi.manage.transform.rsp.TransformListPageRspDTO;
import com.github.pagehelper.PageInfo;
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
     * 取样转化-分页查询
     *
     * @param transformListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "取样转化-分页查询")
    public ResponseResult<PageInfo<TransformListPageRspDTO>> listPage(@Validated @RequestBody TransformListPageReqDTO transformListPageReqDTO) {
            return ResponseResult.getSuccess(transformService.listPage(transformListPageReqDTO));
    }

    /**
     * 取样转化-实施方案维度查询转化信息
     */
    @PostMapping("/listByVectorTask")
    @WebLog(desc = "取样转化-实施方案维度查询转化信息")
    public ResponseResult<List<TransformListByVectorTaskRspDTO>> listByVectorTask(@Validated @RequestBody TransformListByVectorTaskReqDTO transformListByVectorTaskReqDTO) {
        List<TransformListByVectorTaskRspDTO> transformListByVectorTaskRspDTOList = transformService.listByVectorTask(transformListByVectorTaskReqDTO);
        return ResponseResult.getSuccess(transformListByVectorTaskRspDTOList);
    }

    /**
     * 取样转化-审批通过转化列表查询
     */
    @PostMapping("/approvePassTransformQuery")
    @WebLog(desc = "取样转化-审批通过转化列表查询")
    public ResponseResult<List<ApprovePassTransformQueryRspDTO>> approvePassTransformQuery(@RequestBody @Validated ApprovePassTransformQueryReqDTO approvePassTransformQueryReqDTO) {
        List<ApprovePassTransformQueryRspDTO> list = transformService.approvePassTransformQuery(approvePassTransformQueryReqDTO);
        return ResponseResult.getSuccess(list);

    }


}
