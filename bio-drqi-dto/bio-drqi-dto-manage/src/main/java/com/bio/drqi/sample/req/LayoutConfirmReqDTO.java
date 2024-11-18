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

    public void fillSampleToNinetySixList(String vectorTaskCode, String transFormCode, String sampleCode, String identifyPrimer) {
        //找到最新的一个孔板
        List<List<SampleUnitDTO>> lastNinetySixList = this.ninetySixList.get(this.ninetySixList.size() - 1);
        //判断该孔板中是否有数据据，如果没有则直接插入
        if (lastNinetySixList.size() == 0) {
            List<SampleUnitDTO> sampleUnitDTOList=new ArrayList<>();
            sampleUnitDTOList.add(new SampleUnitDTO(vectorTaskCode, transFormCode, sampleCode, identifyPrimer));
            lastNinetySixList.add(sampleUnitDTOList);
        } else {
            //找到最新的一行
            List<SampleUnitDTO> lastRow = lastNinetySixList.get(lastNinetySixList.size() - 1);
            if (lastRow.size() < 12) {
                lastRow.add(new SampleUnitDTO(vectorTaskCode, transFormCode, sampleCode, identifyPrimer));
            } else {
                //如果已经满行，则判断是否满孔板
                if (lastNinetySixList.size() < 8) {
                    List<SampleUnitDTO> sampleUnitDTOList=new ArrayList<>();
                    sampleUnitDTOList.add(new SampleUnitDTO(vectorTaskCode, transFormCode, sampleCode, identifyPrimer));
                    lastNinetySixList.add(sampleUnitDTOList);
                } else {
                    //如果满盘则新增一个孔板
                    this.ninetySixList.add(new ArrayList<List<SampleUnitDTO>>());
                    //再次进行数据插入
                    fillSampleToNinetySixList(vectorTaskCode, transFormCode, sampleCode, identifyPrimer);
                }
            }
        }
    }



    //补充96孔板到满版
    public LayoutConfirmReqDTO restockSampleData() {
        for (List<List<SampleUnitDTO>> layout : this.ninetySixList) {
            //补满行
            while (layout.size() < 8) {
                layout.add(new ArrayList<SampleUnitDTO>());
            }
            //补满列
            for (List<SampleUnitDTO> row : layout) {
                while (row.size()<12){
                    row.add(new SampleUnitDTO());
                }
            }

        }
        return this;
    }
}
