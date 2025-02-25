package com.bio.drqi.manage.service.project;

import com.bio.drqi.step.req.ApproveStepReqDTO;
import com.bio.drqi.step.req.CancelSubmitStepReqDTO;
import com.bio.drqi.step.req.QueryStepReqDTO;
import com.bio.drqi.step.req.SubmitStepReqDTO;
import com.bio.drqi.step.rsp.QueryStepRspDTO;

import java.util.List;

public interface FlowStepService {

    /***
     * 项目执行详情
     */
    List<QueryStepRspDTO> queryStep(QueryStepReqDTO queryStepReqDTO);

    /***
     * 完成项目本步骤
     */
    void submit( SubmitStepReqDTO submitStepReqDTO);

    /***
     * 取消完成本步骤
     */
    void cancelSubmit(CancelSubmitStepReqDTO cancelSubmitStepReqDTO);

    /***
     * 审批本步骤
     */
    void approve(ApproveStepReqDTO approveStepReqDTO);





}
