package com.bio.drqi.ai.toolservice.controller;

import com.bio.drqi.ai.toolservice.dto.AiManageQueryReqDTO;
import com.bio.drqi.ai.toolservice.dto.AiToolTableRspDTO;
import com.bio.drqi.ai.toolservice.service.AiManageQueryToolService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/tool/manage")
public class AiManageQueryToolController {

    private final AiManageQueryToolService aiManageQueryToolService;

    public AiManageQueryToolController(AiManageQueryToolService aiManageQueryToolService) {
        this.aiManageQueryToolService = aiManageQueryToolService;
    }

    @PostMapping("/project/query")
    public AiToolTableRspDTO queryProjects(@RequestBody AiManageQueryReqDTO req) {
        return aiManageQueryToolService.queryProjects(req);
    }

    @PostMapping("/implementation-plan/query")
    public AiToolTableRspDTO queryImplementationPlans(@RequestBody AiManageQueryReqDTO req) {
        return aiManageQueryToolService.queryImplementationPlans(req);
    }

    @PostMapping("/implementation-plan/execution-detail")
    public AiToolTableRspDTO queryImplementationExecutionDetail(@RequestBody AiManageQueryReqDTO req) {
        return aiManageQueryToolService.queryImplementationExecutionDetail(req);
    }

    @PostMapping("/transform/query")
    public AiToolTableRspDTO queryTransforms(@RequestBody AiManageQueryReqDTO req) {
        return aiManageQueryToolService.queryTransforms(req);
    }

    @PostMapping("/sample-test/query")
    public AiToolTableRspDTO querySampleTests(@RequestBody AiManageQueryReqDTO req) {
        return aiManageQueryToolService.querySampleTests(req);
    }

    @PostMapping("/plant/query")
    public AiToolTableRspDTO queryPlantApplies(@RequestBody AiManageQueryReqDTO req) {
        return aiManageQueryToolService.queryPlantApplies(req);
    }

    @PostMapping("/seed-stock/query")
    public AiToolTableRspDTO querySeedStocks(@RequestBody AiManageQueryReqDTO req) {
        return aiManageQueryToolService.querySeedStocks(req);
    }
}
