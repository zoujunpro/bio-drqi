package com.bio.drqi.es.handler;

import com.bio.drqi.es.dto.CanalMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CanalMessageHandler {


    /**
     * {
     *   "id": 123456789,
     *   "database": "bioinfo",
     *   "table": "task",
     *   "pkNames": ["id"],
     *   "isDdl": false,
     *   "type": "UPDATE",
     *   "es": 1713920000000,
     *   "ts": 1713920001000,
     *   "sql": "",
     *   "data": [
     *     {
     *       "id": "10001",
     *       "task_name": "任务标题",
     *       "task_content": "任务内容",
     *       "project_id": "2001",
     *       "owner_id": "u001",
     *       "status": "doing",
     *       "update_time": "2026-04-24 10:00:00"
     *     }
     *   ],
     *   "old": [
     *     {
     *       "status": "todo"
     *     }
     *   ]
     * }
     * @param message
     * @throws Exception
     */
    public void handle(CanalMessage message) throws Exception {
        if (Boolean.TRUE.equals(message.getIsDdl())) {
            return;
        }

        String table = message.getTable();
        String type = message.getType();

        if (message.getData() == null || message.getData().isEmpty()) {
            return;
        }

        for (Map<String, Object> row : message.getData()) {
            String id = String.valueOf(row.get("id"));

            if ("DELETE".equalsIgnoreCase(type)) {
                handleDelete(table, id);
            } else if ("INSERT".equalsIgnoreCase(type) || "UPDATE".equalsIgnoreCase(type)) {
                handleInsertOrUpdate(table, id);
            }
        }
    }

    private void handleInsertOrUpdate(String table, String id) throws Exception {

    }

    private void handleDelete(String table, String id) throws Exception {


    }

}