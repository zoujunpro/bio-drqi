package com.bio.drqi.es.support.search.builder.biotest;

import com.bio.drqi.es.support.search.AbstractBioDrqiSearchDocumentBuilder;

public abstract class AbstractBioTestSearchDocumentBuilder<T> extends AbstractBioDrqiSearchDocumentBuilder<T> {

    @Override
    public String businessCode() {
        return "biotest";
    }

}
