package com.bio.drqi.es.support.search.builder.plant;

import com.bio.drqi.es.support.search.AbstractBioDrqiSearchDocumentBuilder;

public abstract class AbstractPlantSearchDocumentBuilder<T> extends AbstractBioDrqiSearchDocumentBuilder<T> {

    @Override
    public String businessCode() {
        return "plant";
    }
}
