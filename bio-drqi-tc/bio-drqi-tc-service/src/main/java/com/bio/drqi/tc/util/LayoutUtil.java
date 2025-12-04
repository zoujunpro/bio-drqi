package com.bio.drqi.tc.util;

import com.bio.drqi.domain.TcSampleTestTb;
import com.bio.drqi.tc.SampleUnitDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class LayoutUtil {


    public static List<List<List<SampleUnitDTO>>> fillSampleToNinetySixList(List<TcSampleTestTb> tcSampleTestTbList) {
        List<List<List<SampleUnitDTO>>> ninetySixList = new ArrayList<List<List<SampleUnitDTO>>>();
        // 根据引物区分
        Map<String, List<TcSampleTestTb>> identifyPrimerListMap = tcSampleTestTbList.stream().collect(Collectors.groupingBy(TcSampleTestTb::getIdentifyPrimer));
        //
        identifyPrimerListMap.forEach((identifyPrimer, identifyPrimerList) -> {
            Map<String, List<TcSampleTestTb>> listMap = identifyPrimerList.stream().collect(Collectors.groupingBy(TcSampleTestTb::getVectorTaskCode));
            ninetySixList.add(new ArrayList<List<SampleUnitDTO>>());
            listMap.forEach((vectorTaskCode, vectorTaskCerSampleTestTbList) -> {
                //找到最新的一个孔板,并插入新行
                List<List<SampleUnitDTO>> lastNinetySixList = ninetySixList.get(ninetySixList.size() - 1);
                lastNinetySixList.add(new ArrayList<SampleUnitDTO>());
                vectorTaskCerSampleTestTbList.stream().sorted(Comparator.comparing(TcSampleTestTb::getId)).forEach(tcSampleTestTb -> {
                    fillSampleToNinetySixList(ninetySixList, tcSampleTestTb.getVectorTaskCode(), tcSampleTestTb.getExperimentNum(), tcSampleTestTb.getRegionNum(), tcSampleTestTb.getSeedNum(),tcSampleTestTb.getSampleCode(),tcSampleTestTb.getIdentifyPrimer(),tcSampleTestTb.getTcSampleCode());
                });
            });
        });
        restockSampleData(ninetySixList);
        return ninetySixList;
    }

    private static void fillSampleToNinetySixList(List<List<List<SampleUnitDTO>>> ninetySixList, String vectorTaskCode, String experimentCode,String regionNum,String seedNum, String sampleCode, String identifyPrimer,String tcSampleCode) {
        //找到最新的一个孔板
        List<List<SampleUnitDTO>> lastNinetySixList = ninetySixList.get(ninetySixList.size() - 1);
        //找到最新的一行
        List<SampleUnitDTO> lastRow = lastNinetySixList.get(lastNinetySixList.size() - 1);
        if (lastRow.size() < 12&&lastNinetySixList.size()<8) {
            lastRow.add(new SampleUnitDTO(vectorTaskCode, experimentCode,regionNum,seedNum, sampleCode, identifyPrimer,tcSampleCode));
        } else if(lastRow.size() < 8&&lastNinetySixList.size()==8){
            lastRow.add(new SampleUnitDTO(vectorTaskCode, experimentCode,regionNum,seedNum, sampleCode, identifyPrimer,tcSampleCode));
        }else {
            //如果已经满行，则判断是否满孔板
             if (lastNinetySixList.size() < 8) {
                List<SampleUnitDTO> sampleUnitDTOList = new ArrayList<>();
                sampleUnitDTOList.add(new SampleUnitDTO(vectorTaskCode, experimentCode,regionNum,seedNum, sampleCode, identifyPrimer,tcSampleCode));
                lastNinetySixList.add(sampleUnitDTOList);
            } else {
                //如果满盘则新增一个孔板
                List layout = new ArrayList<List<SampleUnitDTO>>();
                layout.add(new ArrayList<SampleUnitDTO>());
                ninetySixList.add(layout);
                //再次进行数据插入
                fillSampleToNinetySixList(ninetySixList, vectorTaskCode, experimentCode,regionNum,seedNum, sampleCode, identifyPrimer,tcSampleCode);
            }
        }
    }


    //补充96孔板到满版
    private static void restockSampleData(List<List<List<SampleUnitDTO>>> ninetySixList) {
        for (List<List<SampleUnitDTO>> layout : ninetySixList) {
            //补满行
            while (layout.size() < 8) {
                layout.add(new ArrayList<SampleUnitDTO>());
            }
            //补满列
            for (List<SampleUnitDTO> row : layout) {
                while (row.size() < 12) {
                    row.add(new SampleUnitDTO());
                }
            }
        }
    }
}
