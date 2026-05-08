package com.bio.drqi.es.support.global.project.seed;

import com.bio.drqi.es.support.global.AbstractBioDrqiGlobalSearchDocumentBuilder;

public abstract class AbstractSeedGlobalSearchDocumentBuilder extends AbstractBioDrqiGlobalSearchDocumentBuilder {

    @Override
    public String businessCode() {
        return "seed";
    }
}
