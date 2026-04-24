package com.bio.drqi.es.sync;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CanalBinlogEvent {

    private String database;

    private String table;

    private String type;

    private Boolean isDdl;

    private List<Map<String, Object>> data;
}
