package com.bio.drqi.es.dto;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public  class EsPageResult {
    private List<String> ids = Collections.emptyList();
    private List<Map<String, Object>> records = Collections.emptyList();
    private Object[] nextSearchAfter;
    private long total;
    private boolean hasNext;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<Map<String, Object>> getRecords() {
        return records;
    }

    public void setRecords(List<Map<String, Object>> records) {
        this.records = records;
    }

    public Object[] getNextSearchAfter() {
        return nextSearchAfter;
    }

    public void setNextSearchAfter(Object[] nextSearchAfter) {
        this.nextSearchAfter = nextSearchAfter;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}