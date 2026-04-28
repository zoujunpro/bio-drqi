package com.bio.drqi.es.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CanalMessage {
    private String database;
    private String table;
    private List<String> pkNames;
    private String type;
    private Boolean isDdl;
    private List<Map<String, Object>> data;
    private List<Map<String, Object>> old;
}
