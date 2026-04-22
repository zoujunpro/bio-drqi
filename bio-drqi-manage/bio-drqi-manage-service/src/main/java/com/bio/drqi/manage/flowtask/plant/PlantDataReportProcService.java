package com.bio.drqi.manage.flowtask.plant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.PlantStatusEnum;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.PlantSingleStockTb;
import com.bio.drqi.manage.dto.plant.PlantDataReportDTO;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.PlantSingleStockTbMapper;
import com.bio.flow.dto.BioHtmlModelDTO;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("plant_data_report")
@Slf4j
public class PlantDataReportProcService extends AbstractPlantBaseTaskService {

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        PlantDataReportDTO plantDataReportDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantDataReportDTO.class);
        if (CollectionUtil.isEmpty(plantDataReportDTO.getContentList())) {
            throw new BusinessException("excel中没有数据");
        }
        for (PlantDataReportDTO.Content content : plantDataReportDTO.getContentList()) {
            PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(content.getPlantCode());
            if (plantSingleStockTb == null) {
                throw new BusinessException("找不到此种植编号:" + content.getPlantCode());
            }
            if (StringUtil.isNotEmpty(content.getPlantStatus())) {
                if (PlantStatusEnum.getCodeByDesc(content.getPlantStatus()) == null) {
                    throw new BusinessException("植株状态异常：" + content.getPlantStatus());
                }
            }
        }
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            PlantDataReportDTO plantDataReportDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantDataReportDTO.class);
            for (PlantDataReportDTO.Content content : plantDataReportDTO.getContentList()) {
                PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(content.getPlantCode());
                if (plantSingleStockTb == null) {
                    throw new BusinessException("找不到此种植编号:" + content.getPlantCode());
                }
                plantSingleStockTb.setPollinationDate(content.getPollinationDate());
                plantSingleStockTb.setVernalizationEndDate(content.getVernalizationEndDate());
                plantSingleStockTb.setVernalizationBeginDate(content.getVernalizationBeginDate());
                plantSingleStockTb.setTransplantDate(content.getTransplantDate());
                plantSingleStockTb.setPlantDate(content.getPlantDate());
                plantSingleStockTb.setPollinationMethod(content.getPollinationMethod());
                plantSingleStockTb.setHarvestDate(content.getHarvestDate());
                if (StringUtil.isNotEmpty(content.getPlantStatus())) {
                    plantSingleStockTb.setPlantStatus(PlantStatusEnum.getCodeByDesc(content.getPlantStatus()));
                }
                plantSingleStockTbMapper.updateById(plantSingleStockTb);
            }
        }


    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        PlantDataReportDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantDataReportDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("数据文件", dto.getExcelUrl()));
        applyFields.add(buildField("数据条数", String.valueOf(CollectionUtil.isEmpty(dto.getContentList()) ? 0 : dto.getContentList().size())));
        sections.add(buildFieldSection("申请信息", applyFields));

        if (CollectionUtil.isEmpty(dto.getContentList())) {
            return sections;
        }

        Map<String, PlantSingleStockTb> plantMap = dto.getContentList().stream()
                .map(PlantDataReportDTO.Content::getPlantCode)
                .filter(StringUtil::isNotEmpty)
                .distinct()
                .map(plantSingleStockTbMapper::selectOneByPlantCode)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toMap(PlantSingleStockTb::getPlantCode, item -> item, (left, right) -> left));
        Map<String, String> speciesNameMap = cerSpeciesConfMapper.selectAll().stream()
                .collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName, (left, right) -> left));
        Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));

        List<String> headers = java.util.Arrays.asList(
                "种植编号", "物种", "品种", "植株状态", "播种/移苗日期", "移栽日期",
                "春化开始日期", "春化结束日期", "授粉方式", "授粉时间", "收获日期",
                "拔节期", "散粉期", "吐丝期", "抽穗期", "始花期", "盛花期", "扬花期", "鼓粒期", "成熟期"
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        for (PlantDataReportDTO.Content content : dto.getContentList()) {
            PlantSingleStockTb plant = plantMap.get(content.getPlantCode());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("种植编号", content.getPlantCode());
            row.put("物种", plant == null ? "" : speciesNameMap.getOrDefault(plant.getSpeciesCode(), plant.getSpeciesCode()));
            row.put("品种", plant == null ? "" : breedNameMap.getOrDefault(plant.getBreedCode(), plant.getBreedCode()));
            row.put("植株状态", StringUtil.isNotEmpty(content.getPlantStatus()) ? content.getPlantStatus() : plantStatusName(plant == null ? null : plant.getPlantStatus()));
            row.put("播种/移苗日期", content.getPlantDate());
            row.put("移栽日期", content.getTransplantDate());
            row.put("春化开始日期", content.getVernalizationBeginDate());
            row.put("春化结束日期", content.getVernalizationEndDate());
            row.put("授粉方式", content.getPollinationMethod());
            row.put("授粉时间", content.getPollinationDate());
            row.put("收获日期", content.getHarvestDate());
            row.put("拔节期", content.getBa_jie_qi());
            row.put("散粉期", content.getShan_fen_qi());
            row.put("吐丝期", content.getTu_si_qi());
            row.put("抽穗期", content.getChou_hui_qi());
            row.put("始花期", content.getShi_hua_qi());
            row.put("盛花期", content.getSheng_hua_qi());
            row.put("扬花期", content.getYang_hua_qi());
            row.put("鼓粒期", content.getGu_li_qi());
            row.put("成熟期", content.getCheng_shu_qi());
            rows.add(row);
        }
        sections.add(buildTableSection("上报明细", headers, rows));
        return sections;
    }

    private String plantStatusName(String code) {
        if (PlantStatusEnum.STATUS_1.code.equals(code)) {
            return PlantStatusEnum.STATUS_1.desc;
        }
        if (PlantStatusEnum.STATUS_2.code.equals(code)) {
            return PlantStatusEnum.STATUS_2.desc;
        }
        if (PlantStatusEnum.STATUS_3.code.equals(code)) {
            return PlantStatusEnum.STATUS_3.desc;
        }
        if (PlantStatusEnum.STATUS_4.code.equals(code)) {
            return PlantStatusEnum.STATUS_4.desc;
        }
        if (PlantStatusEnum.STATUS_5.code.equals(code)) {
            return PlantStatusEnum.STATUS_5.desc;
        }
        if (PlantStatusEnum.STATUS_6.code.equals(code)) {
            return PlantStatusEnum.STATUS_6.desc;
        }
        return code;
    }
}
