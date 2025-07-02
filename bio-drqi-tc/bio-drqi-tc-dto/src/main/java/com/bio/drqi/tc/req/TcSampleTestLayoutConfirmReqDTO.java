package com.bio.drqi.tc.req;

import com.bio.drqi.tc.SampleUnitDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TcSampleTestLayoutConfirmReqDTO {

    private String applyNo;


    /**
     * 单管集合
     */
    private List<SampleUnitDTO> singleList = new ArrayList<>();

    /**
     * 96孔板集合
     */
    private List<List<List<SampleUnitDTO>>> ninetySixList = new ArrayList<List<List<SampleUnitDTO>>>();

    public void fillSampleToSingleList(String vectorTaskCode, String experimentCode, String regionNum, String seedNum, String sampleCode, String identifyPrimer,String tcSampleCode) {
        singleList.add(new SampleUnitDTO(vectorTaskCode, experimentCode, regionNum, seedNum, sampleCode, identifyPrimer,tcSampleCode));
    }
}
