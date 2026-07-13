package com.bio.drqi.es.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.es.dto.EsDocCheckDTO;
import com.bio.drqi.es.dto.EsIndexMonitorStatusDTO;
import com.bio.drqi.es.dto.EsIndexMonitorTaskPageDTO;
import com.bio.drqi.es.service.EsIndexMonitorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/index")
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class EsIndexMonitorController {

    private final EsIndexMonitorService esIndexMonitorService;

    public EsIndexMonitorController(EsIndexMonitorService esIndexMonitorService) {
        this.esIndexMonitorService = esIndexMonitorService;
    }

    @GetMapping("/status/list")
    public ResponseResult<List<EsIndexMonitorStatusDTO>> listStatus() {
        return ResponseResult.getSuccess(esIndexMonitorService.listStatus());
    }

    @PostMapping("/check/{indexCode}")
    public ResponseResult<EsIndexMonitorStatusDTO> check(@PathVariable String indexCode,
                                                         @RequestHeader(value = "userId", required = false) String operatorId,
                                                         @RequestHeader(value = "username", required = false) String operatorName) {
        return ResponseResult.getSuccess(esIndexMonitorService.check(indexCode, operatorId, operatorName));
    }

    @PostMapping("/rebuild/{indexCode}")
    public ResponseResult<EsIndexMonitorStatusDTO> rebuild(@PathVariable String indexCode,
                                                           @RequestHeader(value = "userId", required = false) String operatorId,
                                                           @RequestHeader(value = "username", required = false) String operatorName) {
        return ResponseResult.getSuccess(esIndexMonitorService.rebuild(indexCode, operatorId, operatorName));
    }

    @GetMapping("/task/list")
    public ResponseResult<EsIndexMonitorTaskPageDTO> listTasks(@RequestParam(value = "indexCode", required = false) String indexCode,
                                                               @RequestParam(value = "taskType", required = false) String taskType,
                                                               @RequestParam(value = "status", required = false) String status,
                                                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                               @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ResponseResult.getSuccess(esIndexMonitorService.listTasks(indexCode, taskType, status, pageNum, pageSize));
    }

    @GetMapping("/doc/check")
    public ResponseResult<EsDocCheckDTO> checkDoc(@RequestParam("tableName") String tableName,
                                                  @RequestParam("bizId") String bizId) {
        return ResponseResult.getSuccess(esIndexMonitorService.checkDoc(tableName, bizId));
    }
}
