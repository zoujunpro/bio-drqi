package com.bio.drqi.es.support.search.builder.task;

import com.bio.drqi.es.support.search.AbstractBioDrqiSearchDocumentBuilder;

public abstract class AbstractTaskSearchDocumentBuilder<T> extends AbstractBioDrqiSearchDocumentBuilder<T> {

    @Override
    public String businessCode() {
        return "task";
    }
}
