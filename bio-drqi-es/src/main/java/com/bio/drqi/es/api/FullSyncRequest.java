package com.bio.drqi.es.api;

import lombok.Data;

import java.util.List;

@Data
public class FullSyncRequest {

    /**
     * 指定同步规则键，格式：database.table
     * 不传或空数组表示同步所有配置规则
     */
    private List<String> ruleKeys;
}
