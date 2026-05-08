package com.bio.drqi.es.support.global.project.biotest;

import com.bio.drqi.es.support.global.AbstractBioDrqiGlobalSearchDocumentBuilder;

public abstract class AbstractBioTestGlobalSearchDocumentBuilder extends AbstractBioDrqiGlobalSearchDocumentBuilder {

    @Override
    public String businessCode() {
        return "biotest";
    }
}
