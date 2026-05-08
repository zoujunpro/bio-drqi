package com.bio.drqi.es.support.global.project.project;

import com.bio.drqi.es.support.global.AbstractBioDrqiGlobalSearchDocumentBuilder;

public abstract class AbstractProjectGlobalSearchDocumentBuilder extends AbstractBioDrqiGlobalSearchDocumentBuilder {

    @Override
    public String businessCode() {
        return "project";
    }
}
