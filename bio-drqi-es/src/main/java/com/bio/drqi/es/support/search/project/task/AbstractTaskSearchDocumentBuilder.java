package com.bio.drqi.es.support.search.project.task;

import com.bio.drqi.es.support.search.AbstractBioDrqiSearchDocumentBuilder;

public abstract class AbstractTaskSearchDocumentBuilder extends AbstractBioDrqiSearchDocumentBuilder{

    @Override
    public String businessCode() {
        return "task";
    }
}
