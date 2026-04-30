package com.bio.drqi.es.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.es.dto.req.TableSyncReqDTO;
import com.bio.drqi.es.service.EsSyncService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/syn")
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class EsSyncController {

    private final EsSyncService esSyncService;

    public EsSyncController(EsSyncService esSyncService) {
        this.esSyncService = esSyncService;
    }

    /**
     * 单一入口：按表名同步到 ES
     */
    @PostMapping("/table")
    public ResponseResult<String> syncTable(@RequestBody TableSyncReqDTO tableSyncReqDTO) {
        esSyncService.syncTable(tableSyncReqDTO);
        return ResponseResult.getSuccess(tableSyncReqDTO);
    }
}
