package com.bio.drqi.es.support.search.project.project;

import com.bio.drqi.es.support.search.AbstractBioDrqiSearchDocumentBuilder;

public abstract class AbstractProjectSearchDocumentBuilder<T> extends AbstractBioDrqiSearchDocumentBuilder<T> {

    @Override
    public String businessCode() {
        return "project";
    }
}
