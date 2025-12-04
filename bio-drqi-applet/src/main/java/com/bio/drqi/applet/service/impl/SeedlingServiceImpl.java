package com.bio.drqi.applet.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.applet.dto.req.FindPlantFieldReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingRemainReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingRemoveReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingReportReqDTO;
import com.bio.drqi.applet.enums.CheckResultOperateEnum;
import com.bio.drqi.applet.service.SeedlingService;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.CerPlantFixedFieldEnum;
import com.bio.drqi.enums.PlantOperateEnum;
import com.bio.drqi.common.enums.PlantStatusEnum;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SeedlingServiceImpl implements SeedlingService {


    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private CerPlantOperateLogMapper cerPlantOperateLogMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerSpeciesPlantFeaturesConfMapper cerSpeciesPlantFeaturesConfMapper;

    @Override
    public void remove(SeedlingRemoveReqDTO seedlingRemoveReqDTO) {
        PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(seedlingRemoveReqDTO.getPlantCode());
        if (plantSingleStockTb == null) {
            throw new BusinessException("无此种植编号");
        }
        if(PlantStatusEnum.STATUS_3.code.equals(plantSingleStockTb.getPlantStatus())){
            throw new BusinessException("此苗已经剔除");
        }

        plantSingleStockTb.setPlantStatus(PlantStatusEnum.STATUS_3.code);
        plantSingleStockTbMapper.updateById(plantSingleStockTb);

        CerPlantOperateLog cerPlantOperateLog = new CerPlantOperateLog();
        cerPlantOperateLog.setPlantCode(plantSingleStockTb.getPlantCode());
        cerPlantOperateLog.setPictureUrls(JSONUtil.toJsonStr(seedlingRemoveReqDTO.getPictureUrls()));
        cerPlantOperateLog.setRemark(seedlingRemoveReqDTO.getRemark());
        cerPlantOperateLog.setCreateTime(new Date());
        cerPlantOperateLog.setCreateUserId(SecurityContextHolder.getUserId());
        cerPlantOperateLog.setCreateUserName(SecurityContextHolder.getNickName());
        cerPlantOperateLog.setPlantAttribute(null);
        cerPlantOperateLog.setOperateCode(PlantOperateEnum.delete.name());
        cerPlantOperateLog.setOperateName(PlantOperateEnum.delete.getDesc());
        cerPlantOperateLogMapper.insert(cerPlantOperateLog);



    }

    @Override
    public void report(SeedlingReportReqDTO seedlingReportReqDTO) {
        PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(seedlingReportReqDTO.getPlantCode());
        List<CerSpeciesPlantFeaturesConf> cerSpeciesPlantFeaturesConfList = cerSpeciesPlantFeaturesConfMapper.selectAllBySpeciesCodeOrderByOrderNum(plantSingleStockTb.getSpeciesCode());

        List<SeedlingReportReqDTO.Attribute> attributeList = seedlingReportReqDTO.getAttributes();
        if (CollectionUtil.isNotEmpty(attributeList)) {
            for (SeedlingReportReqDTO.Attribute attribute : attributeList) {
                if (CerPlantFixedFieldEnum.harvestDate.fieldEName.equals(attribute.getName())) {
                    plantSingleStockTb.setHarvestDate(DateUtil.format(new Date(), "yyyy-MM-dd"));
                } else if (CerPlantFixedFieldEnum.pollinationDate.fieldEName.equals(attribute.getName())) {
                    plantSingleStockTb.setPollinationDate(DateUtil.format(new Date(), "yyyy-MM-dd"));
                } else if (CerPlantFixedFieldEnum.vernalizationEndDate.fieldEName.equals(attribute.getName())) {
                    plantSingleStockTb.setVernalizationEndDate(DateUtil.format(new Date(), "yyyy-MM-dd"));
                } else if (CerPlantFixedFieldEnum.vernalizationBeginDate.fieldEName.equals(attribute.getName())) {
                    plantSingleStockTb.setVernalizationBeginDate(DateUtil.format(new Date(), "yyyy-MM-dd"));
                } else if (CerPlantFixedFieldEnum.transplantDate.fieldEName.equals(attribute.getName())) {
                    plantSingleStockTb.setTransplantDate(DateUtil.format(new Date(), "yyyy-MM-dd"));
                } else if (CerPlantFixedFieldEnum.plantDate.fieldEName.equals(attribute.getName())) {
                    plantSingleStockTb.setPlantDate(DateUtil.format(new Date(), "yyyy-MM-dd"));
                }
            }

            // json
            List<SeedlingReportReqDTO.Attribute> result = new ArrayList<>();
            Map<String, String> attributeMap = attributeList.stream().collect(Collectors.toMap(SeedlingReportReqDTO.Attribute::getName, SeedlingReportReqDTO.Attribute::getValue));
            for (CerSpeciesPlantFeaturesConf cerSpeciesPlantFeaturesConf : cerSpeciesPlantFeaturesConfList) {
                if (attributeMap.get(cerSpeciesPlantFeaturesConf.getPlantFeaturesName()) != null) {
                    SeedlingReportReqDTO.Attribute attribute = new SeedlingReportReqDTO.Attribute();
                    attribute.setName(cerSpeciesPlantFeaturesConf.getPlantFeaturesName());
                    attribute.setDesc(cerSpeciesPlantFeaturesConf.getPlantFeaturesDesc());
                    attribute.setValue(DateUtil.format(new Date(), "yyyy-MM-dd"));
                    result.add(attribute);
                }
            }
            if (CollectionUtil.isNotEmpty(result)) {
                plantSingleStockTb.setOtherField(JSONUtil.toJsonStr(result));
            }
        }
        plantSingleStockTb.setPlantStatus(seedlingReportReqDTO.getPlantStatus());
        plantSingleStockTbMapper.updateById(plantSingleStockTb);

        CerPlantOperateLog cerPlantOperateLog = new CerPlantOperateLog();
        cerPlantOperateLog.setPlantCode(seedlingReportReqDTO.getPlantCode());
        cerPlantOperateLog.setRemark(seedlingReportReqDTO.getRemark());
        cerPlantOperateLog.setCreateTime(new Date());
        cerPlantOperateLog.setCreateUserId(SecurityContextHolder.getUserId());
        cerPlantOperateLog.setCreateUserName(SecurityContextHolder.getNickName());
        cerPlantOperateLog.setPictureUrls(JSONUtil.toJsonStr(seedlingReportReqDTO.getPictureUrls()));
        cerPlantOperateLog.setPlantAttribute(JSONUtil.toJsonStr(seedlingReportReqDTO.getAttributes()));
        cerPlantOperateLog.setOperateCode(PlantOperateEnum.report.name());
        cerPlantOperateLog.setOperateName(PlantOperateEnum.report.getDesc());
        cerPlantOperateLogMapper.insert(cerPlantOperateLog);

    }

    @Override
    public List<Map<String, String>> findPlantField(FindPlantFieldReqDTO findPlantFieldReqDTO) {
        List<Map<String, String>> mapListResult = new ArrayList<>();
        PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(findPlantFieldReqDTO.getPlantCode());
        if (plantSingleStockTb == null) {
            throw new BusinessException("种植明细不存在");
        }
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(plantSingleStockTb.getVectorTaskCode());
        List<CerSpeciesPlantFeaturesConf> cerSpeciesPlantFeaturesConfList = cerSpeciesPlantFeaturesConfMapper.selectAllBySpeciesCodeOrderByOrderNum(cerVectorTaskTb.getSpeciesCode());
        //去除已经有属性的数据
        if (plantSingleStockTb.getOtherField() != null) {
            List<SeedlingReportReqDTO.Attribute> attributeList = JSONUtil.toList(JSONUtil.toJsonStr(plantSingleStockTb.getOtherField()), SeedlingReportReqDTO.Attribute.class);
            List<String> filedCodeList = attributeList.stream().map(SeedlingReportReqDTO.Attribute::getName).collect(Collectors.toList());
            cerSpeciesPlantFeaturesConfList = cerSpeciesPlantFeaturesConfList.stream().filter(cerSpeciesPlantFeaturesConf -> !filedCodeList.contains(cerSpeciesPlantFeaturesConf.getPlantFeaturesName())).collect(Collectors.toList());
        }

        for (CerSpeciesPlantFeaturesConf cerSpeciesPlantFeaturesConf : cerSpeciesPlantFeaturesConfList) {
            Map<String, String> map = new HashMap<>();
            map.put(cerSpeciesPlantFeaturesConf.getPlantFeaturesName(), cerSpeciesPlantFeaturesConf.getPlantFeaturesDesc());
        }

        if (StringUtils.isEmpty(plantSingleStockTb.getHarvestDate())) {
            Map<String, String> map = new HashMap<>();
            map.put(CerPlantFixedFieldEnum.harvestDate.fieldEName, CerPlantFixedFieldEnum.harvestDate.fieldCName);
            mapListResult.add(map);
        }
        if (StringUtils.isEmpty(plantSingleStockTb.getPollinationDate())) {
            Map<String, String> map = new HashMap<>();
            map.put(CerPlantFixedFieldEnum.pollinationDate.fieldEName, CerPlantFixedFieldEnum.pollinationDate.fieldCName);
            mapListResult.add(map);
        }
        if (StringUtils.isEmpty(plantSingleStockTb.getVernalizationEndDate())) {
            Map<String, String> map = new HashMap<>();
            map.put(CerPlantFixedFieldEnum.vernalizationEndDate.fieldEName, CerPlantFixedFieldEnum.vernalizationEndDate.fieldCName);
            mapListResult.add(map);
        }
        if (StringUtils.isEmpty(plantSingleStockTb.getVernalizationBeginDate())) {
            Map<String, String> map = new HashMap<>();
            map.put(CerPlantFixedFieldEnum.vernalizationBeginDate.fieldEName, CerPlantFixedFieldEnum.vernalizationBeginDate.fieldCName);
            mapListResult.add(map);
        }
        if (StringUtils.isEmpty(plantSingleStockTb.getTransplantDate())) {
            Map<String, String> map = new HashMap<>();
            map.put(CerPlantFixedFieldEnum.transplantDate.fieldEName, CerPlantFixedFieldEnum.transplantDate.fieldCName);
            mapListResult.add(map);
        }
        if (StringUtils.isEmpty(plantSingleStockTb.getPlantDate())) {
            Map<String, String> map = new HashMap<>();
            map.put(CerPlantFixedFieldEnum.plantDate.fieldEName, CerPlantFixedFieldEnum.plantDate.fieldCName);
            mapListResult.add(map);
        }
        return mapListResult;
    }
}
