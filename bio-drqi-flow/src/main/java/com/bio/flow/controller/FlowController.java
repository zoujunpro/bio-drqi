package com.bio.flow.controller;



import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.flow.dto.ApproveDetailRspDTO;
import com.bio.flow.dto.ProcessDetailReqDTO;
import com.bio.flow.dto.ProcessDetailRspDTO;
import com.bio.flow.service.FlowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程相关接口
 */
@RestController
@RequestMapping("/flow")
public class FlowController {

    @Resource
    private FlowService flowService;

    /**
     * 流程视图展示
     *
     * @param instanceId
     * @return
     */
    @GetMapping("/instanceView")
    @WebLog(desc = "流程视图展示")
    public ResponseResult<String> instanceView(@RequestParam String instanceId) {
        return ResponseResult.getSuccess(flowService.instanceView(instanceId));
    }
    /**
     * 流程详情
     *
     * @param processDetailReqDTO
     * @return
     */
    @PostMapping("/processDetail")
    @WebLog(desc = "流程详情")
    public ResponseResult<List<ProcessDetailRspDTO>> processDetail(@RequestBody ProcessDetailReqDTO processDetailReqDTO) {
        List<ProcessDetailRspDTO> resultList = flowService.processDetail(processDetailReqDTO);
        return ResponseResult.getSuccess(resultList);
    }
    /**
     * 执行中和执行完毕审批详情
     *
     * @param instanceId
     * @return
     */
    @GetMapping("/approveDetail")
    @WebLog(desc = "执行中和执行完毕审批详情")
    public ResponseResult<ApproveDetailRspDTO> approveDetail(@RequestParam String instanceId) {
        ApproveDetailRspDTO approveDetailRspDTO = flowService.approveDetail(instanceId);
        return ResponseResult.getSuccess(approveDetailRspDTO);
    }

}
