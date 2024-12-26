package com.bio.drqi.sample.req;

import com.bio.drqi.base.SampleUnitDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LayoutConfirmReqDTO {

    private String applyNo;


    /**
     * 单管集合
     */
    private List<SampleUnitDTO> singleList = new ArrayList<>();

    /**
     * 96孔板集合
     */
    private List<List<List<SampleUnitDTO>>> ninetySixList = new ArrayList<List<List<SampleUnitDTO>>>();

    public void fillSampleToSingleList(String vectorTaskCode, String transFormCode, String sampleCode, String identifyPrimer) {
        singleList.add(new SampleUnitDTO(vectorTaskCode, transFormCode, sampleCode, identifyPrimer));
    }
}
