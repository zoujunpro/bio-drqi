package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.PrintTypeEnum;
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


    @Override
    public PrintRspDTO vectorBuildPrint(VectorBuildPrintReqDTO vectorBuildPrintReqDTO) {
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
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
            printRspDTO.setPrintDataList(printDataSave("vector_label_print", vectorPrintDataList));
            return printRspDTO;
        } else {
            return null;
        }

    }

    @Override
    public PrintRspDTO transFormPrint(TransFormPrintReqDTO transFormPrintReqDTO) {
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
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
            printRspDTO.setPrintDataList(printDataSave("transform_label_print", transFormPrintDataList));
            return printRspDTO;
        } else {
            return null;
        }
    }

    @Override
    public PrintRspDTO samplePrint(SamplePrintReqDTO samplePrintReqDTO) {
        List<SamplePrintData> samplePrintDataList = new ArrayList<>();
        for (SamplePrintReqDTO.Content content : samplePrintReqDTO.getContentList()) {
            List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByVectorTaskCodeAndSampleCode(content.getVectorTaskCode(), content.getSampleCode());
            if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
                BioSampleTestTb bioSampleTestTb = bioSampleTestTbList.get(0);
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(bioSampleTestTb.getVectorTaskCode());
                if (cerVectorTaskTb == null) {
                    throw new BusinessException("数据异常，找不到实施方案信息：" + bioSampleTestTb.getVectorTaskCode());
                }

                SamplePrintData samplePrintData = new SamplePrintData();
                samplePrintData.setVectorTaskCode(bioSampleTestTb.getVectorTaskCode());
                samplePrintData.setTransformCode(bioSampleTestTb.getTransformCode());
                samplePrintData.setSampleCode(bioSampleTestTb.getSampleCode());
                samplePrintData.setTaskNum(bioSampleTestTb.getApplyNo());
                samplePrintData.setBreedName(cerVectorTaskTb.getAcceptorMaterial());
                samplePrintDataList.add(samplePrintData);
            }
        }
        if (CollectionUtil.isNotEmpty(samplePrintDataList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
            if ("large".equals(samplePrintReqDTO.getLabelType())) {
                printRspDTO.setPrintDataList(printDataSave("sample_large_label_print", samplePrintDataList));
            } else {
                printRspDTO.setPrintDataList(printDataSave("sample_small_label_print", samplePrintDataList));
            }
            return printRspDTO;
        } else {
            return null;
        }
    }

    @Override
    public PrintRspDTO layoutPrint(String layoutNumber) {
        LayoutNumberPrintDTO layoutNumberPrintDTO = new LayoutNumberPrintDTO();
        layoutNumberPrintDTO.setLayoutNumber(layoutNumber);
        PrintRspDTO printRspDTO = new PrintRspDTO();
        printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
        printRspDTO.setPrintDataList(printDataSave("layout_number_label_print", Arrays.asList(layoutNumberPrintDTO)));
        return printRspDTO;
    }

    @Override
    public PrintRspDTO plantPrint(PlantPrintReqDTO plantPrintReqDTO) {
        List<PlantPrintData> plantPrintDataList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(plantPrintReqDTO.getContentList())) {
            for (PlantPrintReqDTO.Content content : plantPrintReqDTO.getContentList()) {
                PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(content.getPlantCode());
                if (plantSingleStockTb == null) {
                    throw new BusinessException("取样苗" + content.getPlantCode() + "未形成种植编号");
                }
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(plantSingleStockTb.getSampleCode());
                if(CollectionUtil.isNotEmpty(bioSampleTestTbList)){
                    throw new BusinessException("找不到取样信息");
                }
                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(bioSampleTestTbList.get(0).getTransformCode(), content.getVectorTaskCode());
                if (cerTransformTb == null) {
                    throw new BusinessException("转化信息不存在");
                }

                PlantPrintData plantPrintData = new PlantPrintData();
                plantPrintData.setVectorTaskCode(content.getVectorTaskCode());
                plantPrintData.setTransformCode(cerTransformTb.getTransformCode());
                plantPrintData.setPlantCode(content.getPlantCode());
                plantPrintData.setBreedName(cerTransformTb.getAcceptorMaterial());
                plantPrintData.setPrintNum(content.getPrintNum() == null ? 1 : content.getPrintNum());
                plantPrintDataList.add(plantPrintData);
            }
        }
        if (CollectionUtil.isNotEmpty(plantPrintDataList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
            printRspDTO.setPrintDataList(printDataSave("plant_label_print", plantPrintDataList));
            return printRspDTO;
        }
        return null;
    }

    @Override
    public PrintRspDTO transPrint(TransPrintReqDTO transPrintReqDTO) {
        List<SamplePrintData> samplePrintDataList = new ArrayList<>();
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
                SamplePrintData samplePrintData = new SamplePrintData();
                samplePrintData.setVectorTaskCode(content.getVectorTaskCode());
                samplePrintData.setTransformCode(content.getTransformCode());
                samplePrintData.setSampleCode(content.getSampleCode());
                samplePrintData.setTaskNum(transPrintReqDTO.getTaskNum());
                samplePrintData.setBreedName(cerTransformTb.getAcceptorMaterial());
                samplePrintDataList.add(samplePrintData);
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
        if (CollectionUtil.isNotEmpty(samplePrintDataList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
            printRspDTO.setPrintDataList(printDataSave(PrintTypeEnum.sample_label_large_project_print.name(), samplePrintDataList));
            return printRspDTO;
            //转化大签打印
        } else if (CollectionUtil.isNotEmpty(transformTransPrintDataList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
            printRspDTO.setPrintDataList(printDataSave("transform_trans_print", transformTransPrintDataList));
            return printRspDTO;
        }
        return null;
    }

    @Override
    public PrintRspDTO tissueEmbryoPrint(TissueEmbryoPrintReqDTO transPrintReqDTO) {
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
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
            printRspDTO.setPrintDataList(printDataSave("tissue_embryo_label_print", tissueEmbryoPrintDTOList));
            return printRspDTO;
        }

        return null;
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
