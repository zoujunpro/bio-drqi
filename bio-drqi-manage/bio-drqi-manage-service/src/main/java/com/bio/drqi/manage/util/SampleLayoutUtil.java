package com.bio.drqi.manage.util;

import com.bio.drqi.domain.BioSampleTestTb;
import com.bio.drqi.manage.base.SampleUnitDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class SampleLayoutUtil {


    public static List<List<List<SampleUnitDTO>>> fillSampleToNinetySixList(List<BioSampleTestTb> bioSampleTestTbList) {
        List<List<List<SampleUnitDTO>>> ninetySixList = new ArrayList<List<List<SampleUnitDTO>>>();

        // 根据引物区分
        Map<String, List<BioSampleTestTb>> identifyPrimerListMap = bioSampleTestTbList.stream().collect(Collectors.groupingBy(BioSampleTestTb::getIdentifyPrimer));
        //
        identifyPrimerListMap.forEach((identifyPrimer, identifyPrimerList) -> {
            Map<String, List<BioSampleTestTb>> listMap = identifyPrimerList.stream().collect(Collectors.groupingBy(BioSampleTestTb::getVectorTaskCode));
            ninetySixList.add(new ArrayList<List<SampleUnitDTO>>());
            listMap.forEach((vectorTaskCode, vectorTaskBioSampleTestTbList) -> {
                //找到最新的一个孔板,并插入新行
                List<List<SampleUnitDTO>> lastNinetySixList = ninetySixList.get(ninetySixList.size() - 1);
                //如果最新的一个孔板已经满盘，需要新添加一个孔板
                if (lastNinetySixList.size() == 8) {
                    ninetySixList.add(new ArrayList<List<SampleUnitDTO>>());
                    lastNinetySixList = ninetySixList.get(ninetySixList.size() - 1);
                }
                lastNinetySixList.add(new ArrayList<SampleUnitDTO>());
                vectorTaskBioSampleTestTbList.stream().sorted(Comparator.comparing(BioSampleTestTb::getId)).forEach(bioSampleTestTb -> {
                    fillSampleToNinetySixList(ninetySixList, bioSampleTestTb.getVectorTaskCode(), null, bioSampleTestTb.getSampleCode(), bioSampleTestTb.getIdentifyPrimer());
                });
            });
        });

        restockSampleData(ninetySixList);
        return ninetySixList;
    }

    private static void fillSampleToNinetySixList(List<List<List<SampleUnitDTO>>> ninetySixList, String vectorTaskCode,  String transFormCode, String sampleCode, String identifyPrimer) {
        //找到最新的一个孔板
        List<List<SampleUnitDTO>> lastNinetySixList = ninetySixList.get(ninetySixList.size() - 1);
        //找到最新的一行
        List<SampleUnitDTO> lastRow = lastNinetySixList.get(lastNinetySixList.size() - 1);
        //如果没有满盘
        if (lastNinetySixList.size() < 8) {
            //没有满行
            if (lastRow.size() < 12) {
                lastRow.add(new SampleUnitDTO(vectorTaskCode, null, sampleCode, identifyPrimer));
            } else {
                //满行需要新添加一行
                List<SampleUnitDTO> sampleUnitDTOList = new ArrayList<>();
                sampleUnitDTOList.add(new SampleUnitDTO(vectorTaskCode, null, sampleCode, identifyPrimer));
                lastNinetySixList.add(sampleUnitDTOList);
            }
            //最后一行
        } else if (lastNinetySixList.size() == 8) {
            //没有满盘（最后一行8列）
            if (lastRow.size() < 8) {
                lastRow.add(new SampleUnitDTO(vectorTaskCode, null, sampleCode, identifyPrimer));
            } else {
                //满盘需要新添加一孔板
                List layout = new ArrayList<List<SampleUnitDTO>>();
                layout.add(new ArrayList<SampleUnitDTO>());
                ninetySixList.add(layout);
                //再次进行数据插入
                fillSampleToNinetySixList(ninetySixList, vectorTaskCode, null, sampleCode, identifyPrimer);

            }

        }

/*        if (lastRow.size() < 12&&lastNinetySixList.size()<8) {
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
                 //                //再次进行数据插入
                fillSampleToNinetySixList(ninetySixList, vectorTaskCode, transFormCode, sampleCode, identifyPrimer);
            }
        }*/
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
