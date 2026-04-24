package com.bio.drqi.es.api;

import com.bio.drqi.es.sync.TableToEsSyncService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/es/sync")
@ConditionalOnProperty(prefix = "sync.es", name = "enabled", havingValue = "true")
public class EsSyncController {

    private final TableToEsSyncService tableToEsSyncService;

    public EsSyncController(TableToEsSyncService tableToEsSyncService) {
        this.tableToEsSyncService = tableToEsSyncService;
    }

    /**
     * 单一入口：按表名同步到 ES
     */
    @PostMapping("/table")
    public Map<String, Object> syncTable(@RequestBody TableSyncRequest request) {
        String idField = request.getIdField() == null || request.getIdField().trim().isEmpty() ? "id" : request.getIdField().trim();
        Map<String, Object> result = tableToEsSyncService.syncTable(request.getTableName(), idField);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "表同步完成");
        response.put("data", result);
        return response;
    }
}
