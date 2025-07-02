package com.bio.drqi.tc.rsp;


import com.bio.drqi.tc.SampleUnitDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TcSampleTestLayoutPreviewRspDTO {

    /**
     * 单管集合
     */
    private List<SampleUnitDTO> singleList = new ArrayList<>();

    /**
     * 96孔板集合
     */
    private List<List<List<SampleUnitDTO>>> ninetySixList = new ArrayList<>();


    public void fillSampleToSingleList(String vectorTaskCode, String experimentNum,String regionNum,String seedNum, String sampleCode, String identifyPrimer,String tcSampleCode) {
        singleList.add( new SampleUnitDTO(vectorTaskCode,experimentNum,regionNum,seedNum,sampleCode,identifyPrimer,tcSampleCode));
    }


}
