package com.bio.drqi.es.support.global.project.task;

import com.bio.drqi.es.support.global.AbstractBioDrqiGlobalSearchDocumentBuilder;

public abstract class AbstractTaskGlobalSearchDocumentBuilder extends AbstractBioDrqiGlobalSearchDocumentBuilder {

    @Override
    public String businessCode() {
        return "task";
    }
}
