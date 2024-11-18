package com.bio.cer.sample.rsp;


import com.bio.cer.base.SampleUnitDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LayoutPreviewRspDTO {

    /**
     * 单管集合
     */
    private List<SampleUnitDTO> singleList = new ArrayList<>();

    /**
     * 96孔板集合
     */
    private List<List<SampleUnitDTO>> ninetySixList = new ArrayList<>();


    public void fillSampleToSingleList(String vectorTaskCode, String transFormCode, String sampleCode,String identifyPrimer) {
        singleList.add( new SampleUnitDTO(vectorTaskCode,transFormCode,sampleCode,identifyPrimer));
    }

    public void fillSampleToNinetySixList(String vectorTaskCode, String transFormCode, String sampleCode,String identifyPrimer) {
        List<SampleUnitDTO> lastNinetySixList = this.ninetySixList.get(this.ninetySixList.size() - 1);
        if (lastNinetySixList.size() < 92) {
            lastNinetySixList.add(new SampleUnitDTO(vectorTaskCode,transFormCode,sampleCode,identifyPrimer));
        } else {
            ninetySixList.add(new ArrayList<SampleUnitDTO>());
            lastNinetySixList = this.ninetySixList.get(this.ninetySixList.size() - 1);
            lastNinetySixList.add(new SampleUnitDTO(vectorTaskCode,transFormCode,sampleCode,identifyPrimer));
        }
    }

    //补充96孔板
    public LayoutPreviewRspDTO restockSampleData(){
        for (List<SampleUnitDTO> sampleUnitDTOList:this.ninetySixList){
            while (sampleUnitDTOList.size()<96){
                sampleUnitDTOList.add(new SampleUnitDTO());
            }
        }
        return this;
    }
}
