package com.bio.drqi.manage.util;

import com.bio.drqi.manage.base.SampleUnitDTO;
import com.bio.drqi.domain.CerSampleTestTb;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class LayoutUtil {


    public static List<List<List<SampleUnitDTO>>> fillSampleToNinetySixList(List<CerSampleTestTb> cerSampleTestTbList) {
        List<List<List<SampleUnitDTO>>> ninetySixList = new ArrayList<List<List<SampleUnitDTO>>>();

        // 根据引物区分
        Map<String, List<CerSampleTestTb>> identifyPrimerListMap = cerSampleTestTbList.stream().collect(Collectors.groupingBy(CerSampleTestTb::getIdentifyPrimer));
        //
        identifyPrimerListMap.forEach((identifyPrimer, identifyPrimerList) -> {
            Map<String, List<CerSampleTestTb>> listMap = identifyPrimerList.stream().collect(Collectors.groupingBy(CerSampleTestTb::getVectorTaskCode));
            ninetySixList.add(new ArrayList<List<SampleUnitDTO>>());
            listMap.forEach((vectorTaskCode, vectorTaskCerSampleTestTbList) -> {
                //找到最新的一个孔板,并插入新行
                List<List<SampleUnitDTO>> lastNinetySixList = ninetySixList.get(ninetySixList.size() - 1);
                lastNinetySixList.add(new ArrayList<SampleUnitDTO>());
                vectorTaskCerSampleTestTbList.stream().sorted(Comparator.comparing(CerSampleTestTb::getId)).forEach(cerSampleTestTb -> {
                    fillSampleToNinetySixList(ninetySixList, cerSampleTestTb.getVectorTaskCode(), cerSampleTestTb.getTransformCode(), cerSampleTestTb.getSampleCode(), cerSampleTestTb.getIdentifyPrimer());
                });
            });
        });

        restockSampleData(ninetySixList);
        return ninetySixList;
    }

    private static void fillSampleToNinetySixList(List<List<List<SampleUnitDTO>>> ninetySixList, String vectorTaskCode, String transFormCode, String sampleCode, String identifyPrimer) {
        //找到最新的一个孔板
        List<List<SampleUnitDTO>> lastNinetySixList = ninetySixList.get(ninetySixList.size() - 1);
        //找到最新的一行
        List<SampleUnitDTO> lastRow = lastNinetySixList.get(lastNinetySixList.size() - 1);
        if (lastRow.size() < 12&&lastNinetySixList.size()<8) {
            lastRow.add(new SampleUnitDTO(vectorTaskCode, transFormCode, sampleCode, identifyPrimer));
        } else if(lastRow.size() < 8&&lastNinetySixList.size()==8){
            lastRow.add(new SampleUnitDTO(vectorTaskCode, transFormCode, sampleCode, identifyPrimer));
        }else {
            //如果已经满行，则判断是否满孔板
             if (lastNinetySixList.size() < 8) {
                List<SampleUnitDTO> sampleUnitDTOList = new ArrayList<>();
                sampleUnitDTOList.add(new SampleUnitDTO(vectorTaskCode, transFormCode, sampleCode, identifyPrimer));
                lastNinetySixList.add(sampleUnitDTOList);
            } else {
                //如果满盘则新增一个孔板
                List layout = new ArrayList<List<SampleUnitDTO>>();
                layout.add(new ArrayList<SampleUnitDTO>());
                ninetySixList.add(layout);
                //再次进行数据插入
                fillSampleToNinetySixList(ninetySixList, vectorTaskCode, transFormCode, sampleCode, identifyPrimer);
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
