package com.bio.drqi.manage.controller;

import com.bio.cer.flow.ApproveDetailRspDTO;
import com.bio.cer.flow.ProcessDetailReqDTO;
import com.bio.cer.flow.ProcessDetailRspDTO;
import com.bio.cer.service.FlowService;
import com.bio.common.core.dto.ResponseResult;

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
    public ResponseResult<List<ProcessDetailRspDTO>> processDetail(@RequestBody  ProcessDetailReqDTO processDetailReqDTO) {
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
    public ResponseResult<ApproveDetailRspDTO> approveDetail(@RequestParam String instanceId) {
        ApproveDetailRspDTO approveDetailRspDTO = flowService.approveDetail(instanceId);
        return ResponseResult.getSuccess(approveDetailRspDTO);
    }

}
