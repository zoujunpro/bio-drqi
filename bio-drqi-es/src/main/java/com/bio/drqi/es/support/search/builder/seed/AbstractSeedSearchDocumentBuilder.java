package com.bio.drqi.es.support.search.builder.seed;

import com.bio.drqi.es.support.search.AbstractBioDrqiSearchDocumentBuilder;

public abstract class AbstractSeedSearchDocumentBuilder<T> extends AbstractBioDrqiSearchDocumentBuilder<T> {

    @Override
    public String businessCode() {
        return "seed";
    }
}
