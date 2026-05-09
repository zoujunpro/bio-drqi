package com.bio.drqi.es.support.search.project.plant;

import com.bio.drqi.es.support.search.AbstractBioDrqiSearchDocumentBuilder;

public abstract class AbstractPlantSearchDocumentBuilder extends AbstractBioDrqiSearchDocumentBuilder{

    @Override
    public String businessCode() {
        return "plant";
    }
}
