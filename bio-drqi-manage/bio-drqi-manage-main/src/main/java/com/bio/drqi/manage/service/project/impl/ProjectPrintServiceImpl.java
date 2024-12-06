package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.base.PrintRspDTO;
import com.bio.drqi.domain.CerPlantDtlTb;
import com.bio.drqi.domain.CerSampleTestTb;
import com.bio.drqi.domain.CerTransformTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.enums.SeedMaterialTypeEnum;
import com.bio.drqi.manage.service.project.ProjectPrintService;
import com.bio.drqi.mapper.CerPlantDtlTbMapper;
import com.bio.drqi.mapper.CerSampleTestTbMapper;
import com.bio.drqi.mapper.CerTransformTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.projectPrint.SamplePrintReqDTO;
import com.bio.drqi.projectPrint.TransFormPrintReqDTO;
import com.bio.drqi.projectPrint.TransPrintReqDTO;
import com.bio.drqi.projectPrint.VectorBuildPrintReqDTO;
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
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private PrintApi printApi;


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
            transFormPrintData.setPrintNum(content.getPrintNum() == null ? 1 : content.getPrintNum());
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
            List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByVectorTaskCodeAndSampleCode(content.getVectorTaskCode(), content.getSampleCode());
            if (CollectionUtil.isNotEmpty(cerSampleTestTbList)) {
                CerSampleTestTb cerSampleTestTb = cerSampleTestTbList.get(0);
                SamplePrintData samplePrintData = new SamplePrintData();
                samplePrintData.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
                samplePrintData.setPlasmidName(cerSampleTestTb.getPlasmidName());
                samplePrintData.setTransformCode(cerSampleTestTb.getTransformCode());
                samplePrintData.setSampleCode(cerSampleTestTb.getSampleCode());
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
    public PrintRspDTO plantPrint(TransPrintReqDTO transPrintReqDTO) {
        List<PlantPrintData> plantPrintDataList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(transPrintReqDTO.getContentList())) {
            for (TransPrintReqDTO.Content content : transPrintReqDTO.getContentList()) {
                CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCodeAndVectorTaskCode(content.getPlantCode(), content.getVectorTaskCode());
                PlantPrintData plantPrintData = new PlantPrintData();
                plantPrintData.setVectorTaskCode(content.getVectorTaskCode());
                plantPrintData.setTransformCode(cerPlantDtlTb.getTransformCode());
                plantPrintData.setPlantCode(content.getPlantCode());
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
