package com.bio.drqi.es.support.global.project.plant;

import com.bio.drqi.es.support.global.AbstractBioDrqiGlobalSearchDocumentBuilder;

public abstract class AbstractPlantGlobalSearchDocumentBuilder extends AbstractBioDrqiGlobalSearchDocumentBuilder {

    @Override
    public String businessCode() {
        return "plant";
    }
}
