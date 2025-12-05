package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.PrintSizeEnum;
import com.bio.drqi.common.enums.PrintTypeEnum;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.base.PrintRspDTO;
import com.bio.drqi.enums.SeedMaterialTypeEnum;
import com.bio.drqi.manage.service.project.ProjectPrintService;
import com.bio.drqi.mapper.*;
import com.bio.drqi.manage.projectPrint.*;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.print.*;
import com.bio.print.api.PrintApi;
import com.bio.print.req.PrintDataReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectPrintServiceImpl implements ProjectPrintService {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;


    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private PrintApi printApi;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;


    @Override
    public List<PrintRspDTO> vectorBuildPrint(VectorBuildPrintReqDTO vectorBuildPrintReqDTO) {
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        List<VectorPrintData> vectorPrintDataList = new ArrayList<>();
        for (VectorBuildPrintReqDTO.Content content : vectorBuildPrintReqDTO.getContentList()) {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(content.getVectorTaskCode());
            if (cerVectorTaskTb == null) {
                throw new BusinessException("参数异常：" + content.getVectorTaskCode());
            }
            VectorPrintData vectorPrintData = new VectorPrintData();
            vectorPrintData.setVectorTaskCode(content.getVectorTaskCode());
            vectorPrintData.setPlasmidName(content.getPlasmidName());
            vectorPrintData.setCapacity(content.getCapacity());
            vectorPrintData.setTaskNum(cerVectorTaskTb.getTaskNum());
            vectorPrintData.setConcentration(content.getConcentration());
            vectorPrintDataList.add(vectorPrintData);
        }
        if (CollectionUtil.isNotEmpty(vectorPrintDataList)) {
            printRspDTOList.add(new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave("vector_label_print", vectorPrintDataList)));
        }
        return printRspDTOList;

    }

    @Override
    public List<PrintRspDTO> transFormPrint(TransFormPrintReqDTO transFormPrintReqDTO) {
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        List<TransFormPrintData> transFormPrintDataList = new ArrayList<>();
        for (TransFormPrintReqDTO.Content content : transFormPrintReqDTO.getContentList()) {
            CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(content.getTransformCode(), content.getVectorTaskCode());
            TransFormPrintData transFormPrintData = new TransFormPrintData();
            transFormPrintData.setProjectCode(cerTransformTb.getProjectCode());
            transFormPrintData.setVectorTaskCode(cerTransformTb.getVectorTaskCode());
            transFormPrintData.setTransFormCode(cerTransformTb.getTransformCode());
            transFormPrintData.setPlasmidName(cerTransformTb.getPlasmidName());
            transFormPrintData.setTaskNum(cerTransformTb.getTaskNum());
            transFormPrintData.setPrintNum(content.getPrintNum() == null ? 1 : content.getPrintNum());
            transFormPrintData.setRemark1(content.getRemark1());
            transFormPrintData.setRemark2(content.getRemark2());
            transFormPrintDataList.add(transFormPrintData);
        }
        if (CollectionUtil.isNotEmpty(transFormPrintDataList)) {
            printRspDTOList.add(new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave("transform_label_print", transFormPrintDataList)));
        }
        return printRspDTOList;
    }

    @Override
    public List<PrintRspDTO> samplePrint(SamplePrintReqDTO samplePrintReqDTO) {
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCodeIn(samplePrintReqDTO.getSampleCodeList());
        if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
            throw new BusinessException("找不到打印数据");
        }
        Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        Map<String, List<BioSampleTestTb>> bioSampleTestTbListMap = bioSampleTestTbList.stream().collect(Collectors.groupingBy(BioSampleTestTb::getSourceCode));
        bioSampleTestTbListMap.forEach((sourceCode, list) -> {
            if (SourceCodeEnum.project.name().equals(sourceCode)) {
                printRspDTOList.add(doProjectSamplePrint(list, samplePrintReqDTO.getLabelType(), cerBreedDictMap));
            } else if (SourceCodeEnum.cer.name().equals(sourceCode)) {
                printRspDTOList.add(doCerSamplePrint(list, samplePrintReqDTO.getLabelType(), cerBreedDictMap));
            }
        });
        return printRspDTOList;

    }

    private PrintRspDTO doCerSamplePrint(List<BioSampleTestTb> bioSampleTestTbList, String printType, Map<String, String> cerBreedDictMap) {
        List<SamplePrintData> samplePrintDataList = new ArrayList<>();
        Map<String, List<BioSampleTestTb>> bioSampleTestTbListMap = bioSampleTestTbList.stream().collect(Collectors.groupingBy(BioSampleTestTb::getSampleCode));
        bioSampleTestTbListMap.forEach((sampleCode, list) -> {
            BioSampleTestTb bioSampleTestTb = list.get(0);
            SamplePrintData samplePrintData = new SamplePrintData();
            samplePrintData.setVectorTaskCode(bioSampleTestTb.getVectorTaskCode());
            samplePrintData.setTransformCode(bioSampleTestTb.getTransformCode());
            samplePrintData.setSampleCode(bioSampleTestTb.getSampleCode());
            samplePrintData.setTaskNum(bioSampleTestTb.getApplyNo());
            samplePrintData.setBreedName(cerBreedDictMap.get(bioSampleTestTb.getBreedCode()));
            samplePrintData.setRegionNum(bioSampleTestTb.getRegionNum());
            samplePrintData.setSeedNum(bioSampleTestTb.getSeedNum());
            samplePrintDataList.add(samplePrintData);
        });
        if (PrintSizeEnum.large.name().equals(printType)) {
            return new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.sample_label_large_cer_print.name(), samplePrintDataList));
        } else {
            return new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.sample_label_small_cer_print.name(), samplePrintDataList));
        }
    }

    private PrintRspDTO doProjectSamplePrint(List<BioSampleTestTb> bioSampleTestTbList, String printType, Map<String, String> cerBreedDictMap) {
        List<SamplePrintData> samplePrintDataList = new ArrayList<>();
        Map<String, List<BioSampleTestTb>> bioSampleTestTbListMap = bioSampleTestTbList.stream().collect(Collectors.groupingBy(BioSampleTestTb::getSampleCode));
        bioSampleTestTbListMap.forEach((sampleCode, list) -> {
            BioSampleTestTb bioSampleTestTb = list.get(0);
            SamplePrintData samplePrintData = new SamplePrintData();
            samplePrintData.setVectorTaskCode(bioSampleTestTb.getVectorTaskCode());
            samplePrintData.setTransformCode(bioSampleTestTb.getTransformCode());
            samplePrintData.setSampleCode(bioSampleTestTb.getSampleCode());
            samplePrintData.setTaskNum(bioSampleTestTb.getApplyNo());
            samplePrintData.setBreedName(cerBreedDictMap.get(bioSampleTestTb.getBreedCode()));
            samplePrintDataList.add(samplePrintData);
        });
        if (PrintSizeEnum.large.name().equals(printType)) {
            return new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.sample_label_large_project_print.name(), samplePrintDataList));
        } else {
            return new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.sample_label_small_project_print.name(), samplePrintDataList));
        }

    }

    @Override
    public List<PrintRspDTO> layoutPrint(String layoutNumber) {
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        LayoutNumberPrintDTO layoutNumberPrintDTO = new LayoutNumberPrintDTO();
        layoutNumberPrintDTO.setLayoutNumber(layoutNumber);
        printRspDTOList.add(new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave("layout_number_label_print", Arrays.asList(layoutNumberPrintDTO))));
        return printRspDTOList;
    }

    @Override
    public List<PrintRspDTO> plantPrint(PlantPrintReqDTO plantPrintReqDTO) {
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));

        Map<String, Integer> map = plantPrintReqDTO.getContentList().stream().collect(Collectors.toMap(PlantPrintReqDTO.Content::getPlantCode, PlantPrintReqDTO.Content::getPrintNum));
        if (CollectionUtil.isNotEmpty(plantPrintReqDTO.getContentList())) {
            List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectAllByPlantCodeIn(plantPrintReqDTO.getContentList().stream().map(PlantPrintReqDTO.Content::getPlantCode).collect(Collectors.toList()));
            if (CollectionUtil.isEmpty(plantSingleStockTbList)) {
                throw new BusinessException("暂未生成种植数据");
            }
            Map<String, List<PlantSingleStockTb>> plantSingleStockTbListMap = plantSingleStockTbList.stream().collect(Collectors.groupingBy(PlantSingleStockTb::getSourceCode));
            plantSingleStockTbListMap.forEach((sourceCode, list) -> {
                List<PlantPrintData> plantPrintDataList = new ArrayList<>();
                for (PlantSingleStockTb plantSingleStockTb : list) {
                    List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(plantSingleStockTb.getSampleCode());
                    if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
                        throw new BusinessException("找不到取样信息");
                    }
                    PlantPrintData plantPrintData = new PlantPrintData();
                    plantPrintData.setVectorTaskCode(bioSampleTestTbList.get(0).getVectorTaskCode());
                    plantPrintData.setTransformCode(bioSampleTestTbList.get(0).getTransformCode());
                    plantPrintData.setPlantCode(plantSingleStockTb.getPlantCode());
                    plantPrintData.setRegionNum(bioSampleTestTbList.get(0).getRegionNum());
                    plantPrintData.setSeedNum(bioSampleTestTbList.get(0).getSeedNum());
                    plantPrintData.setBreedName(cerBreedDictMap.get(plantSingleStockTb.getBreedCode()));
                    plantPrintData.setPrintNum(map.get(plantSingleStockTb.getPlantCode()) == null ? 1 : map.get(plantSingleStockTb.getPlantCode()));
                    plantPrintDataList.add(plantPrintData);

                }
                if (SourceCodeEnum.project.name().equals(sourceCode)) {
                    printRspDTOList.add(new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.plant_label_project_print.name(), plantPrintDataList)));

                } else if (SourceCodeEnum.cer.name().equals(sourceCode)) {
                    printRspDTOList.add(new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.plant_label_cer_print.name(), plantPrintDataList)));
                }
            });
        }
        return printRspDTOList;
    }

    @Override
    public List<PrintRspDTO> transPrint(TransPrintReqDTO transPrintReqDTO) {
        Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        List<PlantPrintData> plantPrintDataList = new ArrayList<>();
        List<TransformTransPrintData> transformTransPrintDataList = new ArrayList<>();
        for (TransPrintReqDTO.Content content : transPrintReqDTO.getContentList()) {
            if (StringUtils.isNotEmpty(content.getSampleCode())) {
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByVectorTaskCodeAndSampleCode(content.getVectorTaskCode(), content.getSampleCode());
                if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
                    throw new BusinessException("取样编号找不到");
                }
                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(bioSampleTestTbList.get(0).getTransformCode(), content.getVectorTaskCode());
                if (cerTransformTb == null) {
                    throw new BusinessException("找不到此取样编号的转化信息:" + content.getSampleCode());
                }
                PlantPrintData plantPrintData = new PlantPrintData();
                plantPrintData.setVectorTaskCode(bioSampleTestTbList.get(0).getVectorTaskCode());
                plantPrintData.setTransformCode(bioSampleTestTbList.get(0).getTransformCode());
                plantPrintData.setPlantCode(content.getSampleCode());
                plantPrintData.setRegionNum(bioSampleTestTbList.get(0).getRegionNum());
                plantPrintData.setSeedNum(bioSampleTestTbList.get(0).getSeedNum());
                plantPrintData.setBreedName(cerBreedDictMap.get(bioSampleTestTbList.get(0).getBreedCode()));
                plantPrintData.setPrintNum(1);
                plantPrintDataList.add(plantPrintData);
            } else if (StringUtils.isNotEmpty(content.getTransformCode())) {
                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(content.getTransformCode(), content.getVectorTaskCode());
                if (cerTransformTb == null) {
                    throw new BusinessException("转化信息不存在");
                }
                TransformTransPrintData transformTransPrintData = new TransformTransPrintData();
                transformTransPrintData.setVectorTaskCode(content.getVectorTaskCode());
                transformTransPrintData.setBreedName(cerTransformTb.getAcceptorMaterial());
                transformTransPrintData.setTransformCode(content.getTransformCode());
                transformTransPrintData.setTaskNum(transPrintReqDTO.getTaskNum());
                transformTransPrintData.setPrintNum(content.getPrintNum() == null ? 1 : content.getPrintNum());
                transformTransPrintDataList.add(transformTransPrintData);
            }
        }
        //取样标签打印(大签)
        if (CollectionUtil.isNotEmpty(plantPrintDataList)) {
            printRspDTOList.add(new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.plant_label_project_print.name(), plantPrintDataList)));
            //转化大签打印
        } else if (CollectionUtil.isNotEmpty(transformTransPrintDataList)) {
            printRspDTOList.add(new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.transform_trans_print.name(), transformTransPrintDataList)));
        }
        return printRspDTOList;
    }

    @Override
    public List<PrintRspDTO> tissueEmbryoPrint(TissueEmbryoPrintReqDTO transPrintReqDTO) {
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        List<TissueEmbryoPrintDTO> tissueEmbryoPrintDTOList = new ArrayList<>();
        for (TissueEmbryoPrintReqDTO.Content content : transPrintReqDTO.getContentList()) {
            List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(content.getSampleCode());
            if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
                throw new BusinessException("取样编号不存在：" + content.getSampleCode());
            }
            TissueEmbryoPrintDTO tissueEmbryoPrintDTO = new TissueEmbryoPrintDTO();
            tissueEmbryoPrintDTO.setPrintNum(content.getPrintNum());
            tissueEmbryoPrintDTO.setVectorTaskCode(bioSampleTestTbList.get(0).getVectorTaskCode());
            tissueEmbryoPrintDTO.setTransformCode(bioSampleTestTbList.get(0).getTransformCode());
            tissueEmbryoPrintDTO.setSampleCode(content.getSampleCode());
            tissueEmbryoPrintDTO.setRemark(content.getRemark());
            tissueEmbryoPrintDTOList.add(tissueEmbryoPrintDTO);
        }
        if (CollectionUtil.isNotEmpty(tissueEmbryoPrintDTOList)) {
            printRspDTOList.add(new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.tissue_embryo_label_print.name(), tissueEmbryoPrintDTOList)));
        }
        return printRspDTOList;
    }

    @Override
    public List<PrintRspDTO> plantApplyPrint(BioPrintPlantApplyReqDTO bioPrintPlantApplyReqDTO) {
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        List<PlantApplyPrintDTO> plantApplyPrintDTOList=new ArrayList<>();
        for (BioPrintPlantApplyReqDTO.Content content : bioPrintPlantApplyReqDTO.getContentList()) {
            PlantApplyDetailTb plantApplyDetailTb = plantApplyDetailTbMapper.selectOneByRegionNumAndSeedNum(content.getRegionNum(), content.getSeedNum());
            if (plantApplyDetailTb == null) {
                throw new BusinessException("种植申请无数据，请检查工单是否已经执行完毕");
            }
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(plantApplyDetailTb.getBreedCode());
            if(cerBreedDict==null){
                throw new BusinessException("数据异常，找不到品种信息，当前种植申请的小区号："+content.getRegionNum()+"种子号:"+content.getSeedNum());
            }
            PlantApplyPrintDTO plantApplyPrintDTO = new PlantApplyPrintDTO();
            plantApplyPrintDTO.setRegionNum(plantApplyDetailTb.getRegionNum());
            plantApplyPrintDTO.setSeedNum(plantApplyDetailTb.getSeedNum());
            plantApplyPrintDTO.setBreedName(cerBreedDict.getBreedName());
            plantApplyPrintDTO.setTaskNum(plantApplyDetailTb.getPlantApplyNum());
            plantApplyPrintDTO.setPrintNumber(content.getPrintNumber()==null?0:content.getPrintNumber());
            plantApplyPrintDTOList.add(plantApplyPrintDTO);
        }

        if (CollectionUtil.isNotEmpty(plantApplyPrintDTOList)) {
            printRspDTOList.add(new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.plant_apply_label_print.name(), plantApplyPrintDTOList)));
        }
        return printRspDTOList;
    }

    @Override
    public List<PrintRspDTO> tcExperimentPrint(BioPrintTcExperimentReqDTO bioPrintTcExperimentReqDTO) {
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        List<TcPollinationApplyPrintDTO> tcPollinationApplyPrintDTOList=new ArrayList<>();
        for (BioPrintTcExperimentReqDTO.Content content : bioPrintTcExperimentReqDTO.getContentList()) {
            TcExperimentDesignTb tcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByRegionNumAndSeedNum(content.getRegionNum(), content.getSeedNum());
            if (tcExperimentDesignTb == null) {
                throw new BusinessException("田测试验无数据，请检查工单是否已经执行完毕");
            }
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(tcExperimentDesignTb.getBreedCode());
            if(cerBreedDict==null){
                throw new BusinessException("数据异常，找不到品种信息，当前种植申请的小区号："+content.getRegionNum()+"种子号:"+content.getSeedNum());
            }
            TcPollinationApplyPrintDTO tcPollinationApplyPrintDTO = new TcPollinationApplyPrintDTO();
            tcPollinationApplyPrintDTO.setRegionNum(tcExperimentDesignTb.getRegionNum());
            tcPollinationApplyPrintDTO.setSeedNum(tcExperimentDesignTb.getSeedNum());
            tcPollinationApplyPrintDTO.setBreedName(cerBreedDict.getBreedName());
            tcPollinationApplyPrintDTO.setTaskNum(tcExperimentDesignTb.getExperimentNum());
            tcPollinationApplyPrintDTO.setPrintNumber(content.getPrintNumber()==null?0:content.getPrintNumber());
            tcPollinationApplyPrintDTOList.add(tcPollinationApplyPrintDTO);
        }

        if (CollectionUtil.isNotEmpty(tcPollinationApplyPrintDTOList)) {
            printRspDTOList.add(new PrintRspDTO(SeedMaterialTypeEnum.TYPE_3.printName, printDataSave(PrintTypeEnum.tc_experiment_label_print.name(), tcPollinationApplyPrintDTOList)));
        }
        return printRspDTOList;
    }

    private List<String> printDataSave(String printType, Object printData) {
        PrintDataReqDTO printDataReqDTO = new PrintDataReqDTO();
        printDataReqDTO.setPrintType(printType);
        printDataReqDTO.setPrintData(JSONUtil.toJsonStr(printData));
        ResponseResult<List<String>> responseResult = printApi.printDataSave(printDataReqDTO);
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        return responseResult.getData();
    }
}
