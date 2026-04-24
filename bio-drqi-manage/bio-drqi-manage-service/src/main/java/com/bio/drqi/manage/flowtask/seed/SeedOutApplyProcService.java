package com.bio.drqi.manage.flowtask.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.common.enums.SeedSourceEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.BioDict;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.SeedProduceAddressDict;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.manage.dto.seed.SeedOutDTO;
import com.bio.drqi.mapper.BioDictMapper;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.SeedProduceAddressDictMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 其他出库
 */
@Service("seed_out_apply")
@Slf4j
public class SeedOutApplyProcService extends AbstractSeedTaskService {

    private static final String USE_TO_DESC = "其他";

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private BioDictMapper bioDictMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        SeedOutDTO seedOutDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedOutDTO.class);
        List<SeedOutDTO.ExecuteFormContent> executeFormContentList = seedOutDTO.getExecuteForm().getExecuteFormContentList();
        Map<String, List<SeedOutDTO.ExecuteFormContent>> map = executeFormContentList.stream().collect(Collectors.groupingBy(SeedOutDTO.ExecuteFormContent::getSeedNum));
        if (CollectionUtil.isNotEmpty(map)) {
            map.forEach((seedNum, executeFormContents) -> {
                List<String> numList = executeFormContents.stream().map(SeedOutDTO.ExecuteFormContent::getNum).collect(Collectors.toList());
                BigDecimal numCount = new BigDecimal("0");
                for (String num:numList){
                    numCount=numCount.add(new BigDecimal(num));
                }
                checkSeedStock(seedNum, numCount);
            });
        }
        //不要做序列化，前端给的有自定义数据
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        log.info("种子库出库扣减库存开始：taskNum={} status={}", bioTaskDtlTb.getTaskNum(), bioTaskDtlTb.getTaskStatus());
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            SeedOutDTO seedOutDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedOutDTO.class);
            if (Objects.nonNull(seedOutDTO.getExecuteForm()) && seedOutDTO.getExecuteForm().getExecuteFormContentList().size() > 0) {
                for (int i = 0; i < seedOutDTO.getExecuteForm().getExecuteFormContentList().size(); i++) {
                    SeedOutDTO.ExecuteFormContent executeFormContent = seedOutDTO.getExecuteForm().getExecuteFormContentList().get(i);
                    //扣减库存，记录出库日志
                    reduceSeedStock(executeFormContent.getSeedNum(), bioTaskDtlTb, new BigDecimal(executeFormContent.getNum()), executeFormContent.getRemark(), i + 1, seedOutDTO.getApplyFrom().getUseToDesc());
                }
            }
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        //不做任何处理

    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        SeedOutDTO seedOutDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedOutDTO.class);
        if (seedOutDTO == null || seedOutDTO.getApplyFrom() == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        SeedOutDTO.ApplyFrom applyFrom = seedOutDTO.getApplyFrom();

        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("用途", applyFrom.getUseToDesc()));
        applyFields.add(buildField("出库类型", defaultString(applyFrom.getOutType())));
        applyFields.add(buildField("交付方式", translateDeliverMethod(applyFrom.getDeliverMethod())));
        applyFields.add(buildField("接收人", applyFrom.getReceiverName()));
        applyFields.add(buildField("联系电话", applyFrom.getReceiverTelephone()));
        applyFields.add(buildField("接收地址", applyFrom.getReceiverAddress()));
        applyFields.add(buildField("种子要求", applyFrom.getSeedDemandDesc()));
        applyFields.add(buildField("分装和标签要求", applyFrom.getLabelDemandDesc()));
        applyFields.add(buildField("备注", applyFrom.getApplyRemark()));
        sections.add(buildFieldSection("申请信息", applyFields));

        List<SeedOutDTO.ApplyFromContent> applyContentList = resolveApplyContentList(seedOutDTO, applyFrom);
        if (CollectionUtil.isNotEmpty(applyContentList)) {
            Map<String, String> speciesNameMap = cerSpeciesConfMapper.selectList(null).stream()
                    .collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName, (left, right) -> left));
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            Map<String, String> addressNameMap = seedProduceAddressDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(SeedProduceAddressDict::getAddressCode, SeedProduceAddressDict::getAddressName, (left, right) -> left));
            List<String> headers = Arrays.asList(
                    "种子编号", "项目编号", "项目名称", "子项目编号", "实施方案编号",
                    "基因型", "物种", "品种", "产地", "年份", "发芽率", "性状纯度",
                    "申请数量", "单位", "是否包衣", "备注"
            );
            List<Map<String, Object>> rows = new ArrayList<>();
            for (SeedOutDTO.ApplyFromContent content : applyContentList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("种子编号", content.getSeedNum());
                row.put("项目编号", content.getProjectCode());
                row.put("项目名称", content.getProjectName());
                row.put("子项目编号", content.getSubProjectCode());
                row.put("实施方案编号", content.getVectorTaskCode());
                row.put("基因型", content.getGeneType());
                row.put("物种", resolveSpeciesName(content, speciesNameMap));
                row.put("品种", resolveBreedName(content, breedNameMap));
                row.put("产地", resolveAddressName(content.getProductAddress(), addressNameMap));
                row.put("年份", content.getYear());
                row.put("发芽率", content.getSgr());
                row.put("性状纯度", content.getTpur());
                row.put("申请数量", content.getNum());
                row.put("单位", content.getUnit());
                row.put("是否包衣", translateCoatingFlag(content.getCoatingFlag()));
                row.put("备注", content.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("申请种子信息", headers, rows));
        }

        if (seedOutDTO.getExecuteForm() != null && CollectionUtil.isNotEmpty(seedOutDTO.getExecuteForm().getExecuteFormContentList())) {
            Map<String, String> speciesNameMap = cerSpeciesConfMapper.selectList(null).stream()
                    .collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName, (left, right) -> left));
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            Map<String, String> addressNameMap = seedProduceAddressDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(SeedProduceAddressDict::getAddressCode, SeedProduceAddressDict::getAddressName, (left, right) -> left));

            List<String> headers = Arrays.asList(
                    "种子编号", "出库数量", "备注", "来源", "代次", "物种", "品种",
                    "当前库存", "单位", "收获方式", "授粉方式", "收获时间", "生产地点",
                    "材料类型", "实施方案编号", "试验方案编号", "种植编号"
            );
            List<Map<String, Object>> rows = new ArrayList<>();
            for (SeedOutDTO.ExecuteFormContent content : seedOutDTO.getExecuteForm().getExecuteFormContentList()) {
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(content.getSeedNum());
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("种子编号", content.getSeedNum());
                row.put("出库数量", content.getNum());
                row.put("备注", content.getRemark());
                row.put("来源", seedStockTb == null ? "" : translateSeedSource(seedStockTb.getSourceType()));
                row.put("代次", seedStockTb == null ? "" : GenerationEnum.getGenerationDesc(seedStockTb.getGeneration()));
                row.put("物种", seedStockTb == null ? "" : speciesNameMap.get(seedStockTb.getSpeciesCode()));
                row.put("品种", seedStockTb == null ? "" : breedNameMap.get(seedStockTb.getBreedCode()));
                row.put("当前库存", seedStockTb == null ? "" : formatNumber(seedStockTb.getSeedNumber()));
                row.put("单位", seedStockTb == null ? "" : seedStockTb.getUnit());
                row.put("收获方式", seedStockTb == null ? "" : translateDict(BioDictTypeEnum.HARVEST_TYPE, seedStockTb.getHarvestType()));
                row.put("授粉方式", seedStockTb == null ? "" : translateDict(BioDictTypeEnum.POLLINATE_TYPE, seedStockTb.getPollinationMethod()));
                row.put("收获时间", seedStockTb == null ? "" : seedStockTb.getHarvestTime());
                row.put("生产地点", seedStockTb == null ? "" : addressNameMap.get(seedStockTb.getProductionLocationCode()));
                row.put("材料类型", seedStockTb == null ? "" : translateDict(BioDictTypeEnum.MATERIAL_TYPE, seedStockTb.getMaterialType()));
                row.put("实施方案编号", seedStockTb == null ? "" : seedStockTb.getVectorTaskCode());
                row.put("试验方案编号", seedStockTb == null ? "" : seedStockTb.getExperimentNum());
                row.put("种植编号", seedStockTb == null ? "" : seedStockTb.getPlantCode());
                rows.add(row);
            }
            sections.add(buildTableSection("出库种子明细", headers, rows));
        }

        return sections;
    }

    private String translateDeliverMethod(String deliverMethod) {
        if (StringUtils.isEmpty(deliverMethod)) {
            return "";
        }
        if ("1".equals(deliverMethod)) {
            return "邮寄";
        }
        if ("2".equals(deliverMethod)) {
            return "自提";
        }
        return deliverMethod;
    }

    private String translateCoatingFlag(String coatingFlag) {
        if (StringUtils.isEmpty(coatingFlag)) {
            return "";
        }
        if ("Y".equalsIgnoreCase(coatingFlag)) {
            return "是";
        }
        if ("N".equalsIgnoreCase(coatingFlag)) {
            return "否";
        }
        return coatingFlag;
    }

    private String translateSeedSource(String sourceCode) {
        SeedSourceEnum seedSourceEnum = SeedSourceEnum.getByCode(sourceCode);
        return seedSourceEnum == null ? sourceCode : seedSourceEnum.name;
    }

    private String translateDict(BioDictTypeEnum dictTypeEnum, String dictValueCode) {
        if (StringUtils.isEmpty(dictValueCode)) {
            return "";
        }
        BioDict bioDict = bioDictMapper.selectOneByDictTypeAndDictValueCode(dictTypeEnum.name(), dictValueCode);
        return bioDict == null ? dictValueCode : bioDict.getDictValueName();
    }

    private String formatNumber(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private String resolveSpeciesName(SeedOutDTO.ApplyFromContent content, Map<String, String> speciesNameMap) {
        if (StringUtils.isNotEmpty(content.getSpeciesName())) {
            return content.getSpeciesName();
        }
        if (StringUtils.isEmpty(content.getSpeciesCode())) {
            return "";
        }
        return defaultString(speciesNameMap.getOrDefault(content.getSpeciesCode(), content.getSpeciesCode()));
    }

    private String resolveBreedName(SeedOutDTO.ApplyFromContent content, Map<String, String> breedNameMap) {
        if (StringUtils.isNotEmpty(content.getBreedName())) {
            return content.getBreedName();
        }
        if (StringUtils.isEmpty(content.getBreedCode())) {
            return "";
        }
        return defaultString(breedNameMap.getOrDefault(content.getBreedCode(), content.getBreedCode()));
    }

    private String resolveAddressName(String productAddress, Map<String, String> addressNameMap) {
        if (StringUtils.isEmpty(productAddress)) {
            return "";
        }
        return defaultString(addressNameMap.getOrDefault(productAddress, productAddress));
    }

    private List<SeedOutDTO.ApplyFromContent> resolveApplyContentList(SeedOutDTO seedOutDTO, SeedOutDTO.ApplyFrom applyFrom) {
        if (CollectionUtil.isNotEmpty(applyFrom.getApplyFromContentList())) {
            return applyFrom.getApplyFromContentList();
        }
        if (CollectionUtil.isNotEmpty(applyFrom.getApplyFormContentList())) {
            return applyFrom.getApplyFormContentList();
        }
        if (seedOutDTO.getExecuteForm() == null || CollectionUtil.isEmpty(seedOutDTO.getExecuteForm().getExecuteFormContentList())) {
            return Collections.emptyList();
        }
        List<SeedOutDTO.ApplyFromContent> fallbackList = new ArrayList<>();
        for (SeedOutDTO.ExecuteFormContent executeFormContent : seedOutDTO.getExecuteForm().getExecuteFormContentList()) {
            SeedOutDTO.ApplyFromContent applyFromContent = new SeedOutDTO.ApplyFromContent();
            applyFromContent.setSeedNum(executeFormContent.getSeedNum());
            applyFromContent.setNum(executeFormContent.getNum());
            applyFromContent.setRemark(executeFormContent.getRemark());
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(executeFormContent.getSeedNum());
            if (seedStockTb != null) {
                applyFromContent.setProjectCode(seedStockTb.getProjectCode());
                applyFromContent.setProjectName(seedStockTb.getTargetCharacter());
                applyFromContent.setVectorTaskCode(seedStockTb.getVectorTaskCode());
                applyFromContent.setGeneType(seedStockTb.getGeneType());
                applyFromContent.setSpeciesCode(seedStockTb.getSpeciesCode());
                applyFromContent.setBreedCode(seedStockTb.getBreedCode());
                applyFromContent.setProductAddress(seedStockTb.getProductionLocationCode());
                applyFromContent.setYear(seedStockTb.getHarvestTime());
                applyFromContent.setUnit(seedStockTb.getUnit());
            }
            fallbackList.add(applyFromContent);
        }
        return fallbackList;
    }
}
