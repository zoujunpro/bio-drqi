package com.bio.drqi.manage.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.SampleApplyTypeEnum;
import com.bio.drqi.manage.dto.project.*;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.mapper.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test2")
public class Clean20251030Controller {

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;


    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;


    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;

    @Resource
    private CerVectorStepLogMapper cerVectorStepLogMapper;

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private SeedStockDestructionLogMapper seedStockDestructionLogMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;


    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;


    @Resource
    private BmsProductTbMapper bmsProductTbMapper;


    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;


    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;


    @Resource
    private CerSampleCodePrefixTbMapper cerSampleCodePrefixTbMapper;

    @Resource
    private CerSampleApplyTbMapper cerSampleApplyTbMapper;

    @GetMapping("/cleanSampleApply20251030")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSampleApply() {
        List<CerSampleApplyTb> cerSampleApplyTbList = cerSampleApplyTbMapper.selectSelective(null);
        for (CerSampleApplyTb cerSampleApplyTb : cerSampleApplyTbList) {
            log.info("cerSampleApplyTb={}" + JSONUtil.toJsonStr(cerSampleApplyTb));
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerSampleApplyTb.getApplyNo());
            if (bioTaskDtlTb == null) {
                throw new BusinessException("找不到取样检测信息");
            }
            NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
            cerSampleApplyTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
            cerSampleApplyTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
            cerSampleApplyTb.setApplyDesc(bioTaskDtlTb.getTaskDesc());
            cerSampleApplyTb.setApplyType(CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList()) ? SampleApplyTypeEnum.R.name() : SampleApplyTypeEnum.F.name());
            cerSampleApplyTb.setIdentifyExcelUrl(newSampleTestDTO.getIdentifyPrimerTemplateExcelUrl());
            cerSampleApplyTb.setOneTestExcelUrl(newSampleTestDTO.getTestDataExcelUrl());
            cerSampleApplyTb.setNgsExcelUrl(newSampleTestDTO.getBioInfoResultExcelUrl());
            cerSampleApplyTb.setCloneFlag(newSampleTestDTO.isCloneFlag() ? BioDrQiContents.Y : BioDrQiContents.N);
            cerSampleApplyTb.setLayoutFlag(newSampleTestDTO.getTestType());

            //如果是首次申请，更新申请中包含的事实方案和取样编号范围
            List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
            if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                continue;
            }
            Map<String, List<CerSampleTestTb>> cerSampleTestTbListMap = cerSampleTestTbList.stream().collect(Collectors.groupingBy(CerSampleTestTb::getVectorTaskCode));
            cerSampleApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(cerSampleTestTbListMap.keySet()).replace("[","").replace("]","").replace("\"",""));
            StringBuffer sampleCodeRangeBuff = new StringBuffer();
            if (SampleApplyTypeEnum.F.name().equals(cerSampleApplyTb.getApplyType())) {
                cerSampleTestTbListMap.forEach((vectorTaskCode, sampleTestList) -> {
                    CerSampleCodePrefixTb cerSampleCodePrefixTb = cerSampleCodePrefixTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
                    sampleTestList = sampleTestList.stream().filter(sampleTest->sampleTest.getSampleCode().startsWith(cerSampleCodePrefixTb.getSampleCodePrefix())).sorted(Comparator.comparing(sampleTest -> Integer.valueOf(sampleTest.getSampleCode().substring(2)))).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(sampleTestList)){
                        sampleCodeRangeBuff.append(sampleTestList.get(0).getSampleCode() + "-" + sampleTestList.get(sampleTestList.size() - 1).getSampleCode()).append(",");
                    }
                });
                if(StringUtils.isNotEmpty(sampleCodeRangeBuff.toString())){
                    cerSampleApplyTb.setSampleCodeRange(sampleCodeRangeBuff.substring(0, sampleCodeRangeBuff.length() - 1));
                }
            }

            cerSampleApplyTbMapper.updateById(cerSampleApplyTb);
        }
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 修复种植信息导入缺失一条日期问题
     *
     * @return
     */
    @GetMapping("/cleanPlantDtl20251030")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlantDtl20251030() {
        List<PlantDTlExcel> plantDTlExcelList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\小麦EW00101-01a、02a.xlsx", PlantDTlExcel.class);
        for (PlantDTlExcel plantDTlExcel : plantDTlExcelList) {
            log.info("plantDTlExcel=" + JSONUtil.toJsonStr(plantDTlExcel));
            CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCode(plantDTlExcel.plantNum);
            if (cerPlantDtlTb == null) {
                throw new BusinessException("找不到种植编号");
            }
            cerPlantDtlTb.setHarvestDate(plantDTlExcel.harvestDate);
            cerPlantDtlTbMapper.updateById(cerPlantDtlTb);
        }
        return ResponseResult.getSuccess("ok");
    }

    @Data
    public static class PlantDTlExcel {

        @ExcelProperty("种植编号")
        private String plantNum;

        @ExcelProperty("收获日期")
        private String harvestDate;

    }
}
