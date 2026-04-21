package com.bio.drqi.manage.flowtask.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.BioDict;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.SeedProduceAddressDict;
import com.bio.drqi.domain.SeedStockDestructionLog;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.common.enums.SeedSourceEnum;
import com.bio.drqi.manage.dto.seed.SeedDestructionDTO;
import com.bio.drqi.mapper.BioDictMapper;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.SeedProduceAddressDictMapper;
import com.bio.drqi.mapper.SeedStockDestructionLogMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.flow.dto.BioHtmlModelDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 种子销毁申请
 */
@Service("seed_destruction_apply")
public class SeedDestructionApplyProcService extends AbstractSeedTaskService {

    private static final String USE_TO_DESC = "种子销毁";


    @Resource
    private SeedStockDestructionLogMapper seedStockDestructionLogMapper;

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
        SeedDestructionDTO seedDestructionDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedDestructionDTO.class);
        for (int i = 0; i < seedDestructionDTO.getSeedList().size(); i++) {
            SeedDestructionDTO.SeedDTO seedDTO = seedDestructionDTO.getSeedList().get(i);
            ValidatorUtil.validator(seedDestructionDTO);
            checkSeedStock(seedDTO.getSeedNum(), seedDTO.getSeedNumber());
        }
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {

            SeedDestructionDTO seedDestructionDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedDestructionDTO.class);

            if (CollectionUtil.isEmpty(seedDestructionDTO.getDestructionEvidenceList())) {
                throw new BusinessException("销毁证据缺失");
            }
            if (StringUtils.isEmpty(seedDestructionDTO.getDestructionMethod())) {
                throw new BusinessException("销毁方式缺失");
            }
            if (StringUtils.isEmpty(seedDestructionDTO.getDestructionLocation())) {
                throw new BusinessException("销毁地点缺失");
            }


            for (int i = 0; i < seedDestructionDTO.getSeedList().size(); i++) {
                SeedDestructionDTO.SeedDTO seedDTO = seedDestructionDTO.getSeedList().get(i);
                //扣减冻结库存，记录出库日志
                reduceSeedStock(seedDTO.getSeedNum(), bioTaskDtlTb, seedDTO.getSeedNumber(), seedDTO.getRemarks(), i + 1, USE_TO_DESC);
                //记录销毁信息
                writeSeedDestructionLog(bioTaskDtlTb, seedDTO, seedDestructionDTO);
            }
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }


    private void writeSeedDestructionLog(BioTaskDtlTb bioTaskDtlTb, SeedDestructionDTO.SeedDTO seedDTO, SeedDestructionDTO seedDestructionDTO) {
        SeedStockDestructionLog seedStockDestructionLog = new SeedStockDestructionLog();
        seedStockDestructionLog.setSeedNum(seedDTO.getSeedNum());
        seedStockDestructionLog.setUnit(seedDTO.getUnit());
        seedStockDestructionLog.setSeedNumber(seedDTO.getSeedNumber());
        seedStockDestructionLog.setRemarks(seedDTO.getRemarks());
        seedStockDestructionLog.setTaskNum(bioTaskDtlTb.getTaskNum());
        seedStockDestructionLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        seedStockDestructionLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        seedStockDestructionLog.setDestructionLocation(seedDestructionDTO.getDestructionLocation());
        seedStockDestructionLog.setDestructionMethod(seedDestructionDTO.getDestructionMethod());
        seedStockDestructionLog.setDestructionEvidence(JSONUtil.toJsonStr(seedDestructionDTO.getDestructionEvidenceList()));
        seedStockDestructionLog.setDestructionTime(new Date());
        seedStockDestructionLogMapper.insert(seedStockDestructionLog);
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        SeedDestructionDTO seedDestructionDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedDestructionDTO.class);
        if (seedDestructionDTO == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("销毁方式", defaultString(seedDestructionDTO.getDestructionMethod())));
        applyFields.add(buildField("销毁地点", defaultString(seedDestructionDTO.getDestructionLocation())));
        applyFields.add(buildField("销毁份数", String.valueOf(CollectionUtil.size(seedDestructionDTO.getSeedList()))));
        applyFields.add(buildField("销毁证据数量", String.valueOf(CollectionUtil.size(seedDestructionDTO.getDestructionEvidenceList()))));
        sections.add(buildFieldSection("申请信息", applyFields));

        if (CollectionUtil.isNotEmpty(seedDestructionDTO.getSeedList())) {
            Map<String, String> speciesNameMap = cerSpeciesConfMapper.selectList(null).stream()
                    .collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName, (left, right) -> left));
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            Map<String, String> addressNameMap = seedProduceAddressDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(SeedProduceAddressDict::getAddressCode, SeedProduceAddressDict::getAddressName, (left, right) -> left));

            List<String> headers = Arrays.asList(
                    "种子编号", "销毁数量", "单位", "备注", "来源", "代次", "物种", "品种",
                    "当前库存", "收获方式", "授粉方式", "收获时间", "生产地点", "材料类型",
                    "实施方案编号", "试验方案编号", "种植编号"
            );
            List<Map<String, Object>> rows = new ArrayList<>();
            for (SeedDestructionDTO.SeedDTO seedDTO : seedDestructionDTO.getSeedList()) {
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedDTO.getSeedNum());
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("种子编号", seedDTO.getSeedNum());
                row.put("销毁数量", formatNumber(seedDTO.getSeedNumber()));
                row.put("单位", StringUtils.isNotEmpty(seedDTO.getUnit()) ? seedDTO.getUnit() : seedStockTb == null ? "" : seedStockTb.getUnit());
                row.put("备注", seedDTO.getRemarks());
                row.put("来源", seedStockTb == null ? "" : translateSeedSource(seedStockTb.getSourceType()));
                row.put("代次", seedStockTb == null ? "" : GenerationEnum.getGenerationDesc(seedStockTb.getGeneration()));
                row.put("物种", seedStockTb == null ? "" : speciesNameMap.get(seedStockTb.getSpeciesCode()));
                row.put("品种", seedStockTb == null ? "" : breedNameMap.get(seedStockTb.getBreedCode()));
                row.put("当前库存", seedStockTb == null ? "" : formatNumber(seedStockTb.getSeedNumber()));
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
            sections.add(buildTableSection("销毁种子明细", headers, rows));
        }

        return sections;
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
}
