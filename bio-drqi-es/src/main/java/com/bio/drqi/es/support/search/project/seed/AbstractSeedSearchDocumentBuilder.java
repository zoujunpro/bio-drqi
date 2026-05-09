package com.bio.drqi.es.support.search.project.seed;

import com.bio.drqi.es.support.search.AbstractBioDrqiSearchDocumentBuilder;

public abstract class AbstractSeedSearchDocumentBuilder extends AbstractBioDrqiSearchDocumentBuilder {

    @Override
    public String businessCode() {
        return "seed";
    }
}
