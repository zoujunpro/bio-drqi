package com.bio.drqi.es.support.search.project.project;

import com.bio.drqi.es.support.search.AbstractBioDrqiSearchDocumentBuilder;

public abstract class AbstractProjectSearchDocumentBuilder extends AbstractBioDrqiSearchDocumentBuilder {

    @Override
    public String businessCode() {
        return "project";
    }
}
