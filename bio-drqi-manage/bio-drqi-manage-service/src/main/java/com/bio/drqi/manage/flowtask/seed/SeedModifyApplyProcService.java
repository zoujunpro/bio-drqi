package com.bio.drqi.manage.flowtask.seed;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.domain.PlantSingleStockTb;
import com.bio.drqi.domain.SeedModifyLog;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.manage.dto.seed.SeedModifyTaskDTO;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.PlantSingleStockTbMapper;
import com.bio.drqi.mapper.SeedModifyLogMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.bio.flow.dto.BioHtmlModelDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Service("seed_modify_apply")
public class SeedModifyApplyProcService extends AbstractSeedTaskService {

    private static final Set<String> SEED_STOCK_TB_FIELD_SET = new HashSet<>();
    private static final Map<String, Field> SEED_STOCK_TB_FIELD_MAP = new java.util.HashMap<>();
    private static final String FIELD_PLANT_CODE = "plantCode";
    private static final String FIELD_VECTOR_TASK_CODE = "vectorTaskCode";
    private static final String FIELD_PROJECT_CODE = "projectCode";
    private static final Map<String, String> FIELD_NAME_MAP = new HashMap<>();

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private SeedModifyLogMapper seedModifyLogMapper;

    static {
        Field[] fields = SeedStockTb.class.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && isTableField(field)) {
                SEED_STOCK_TB_FIELD_SET.add(field.getName());
                field.setAccessible(true);
                SEED_STOCK_TB_FIELD_MAP.put(field.getName(), field);
            }
        }
        FIELD_NAME_MAP.put(FIELD_PLANT_CODE, "种植编号");
        FIELD_NAME_MAP.put("seedNum", "种子编号");
        FIELD_NAME_MAP.put("parentNum", "上一代种子编号");
        FIELD_NAME_MAP.put("generation", "代次");
        FIELD_NAME_MAP.put("speciesCode", "物种");
        FIELD_NAME_MAP.put("breedCode", "品种");
        FIELD_NAME_MAP.put("pollinationMethod", "授粉方式");
        FIELD_NAME_MAP.put("harvestType", "收获方式");
        FIELD_NAME_MAP.put("harvestTime", "收获时间");
        FIELD_NAME_MAP.put("seedNumber", "种子数量");
        FIELD_NAME_MAP.put("unit", "单位");
        FIELD_NAME_MAP.put("sourceType", "来源");
        FIELD_NAME_MAP.put("productionLocationCode", "生产地点");
        FIELD_NAME_MAP.put("stockLocationNum", "库位编号");
        FIELD_NAME_MAP.put("remarks", "备注");
        FIELD_NAME_MAP.put("targetCharacter", "目标性状");
        FIELD_NAME_MAP.put("aliasName", "别名");
        FIELD_NAME_MAP.put("geneType", "基因型");
        FIELD_NAME_MAP.put("checkResult", "检测结果");
        FIELD_NAME_MAP.put("materialType", "材料类型");
        FIELD_NAME_MAP.put("matherSeedNum", "母本种子编号");
        FIELD_NAME_MAP.put("fatherSeedNum", "父本种子编号");
        FIELD_NAME_MAP.put("matherRegionNum", "母本小区编号");
        FIELD_NAME_MAP.put("fatherRegionNum", "父本小区编号");
        FIELD_NAME_MAP.put("genealogy", "系谱");
        FIELD_NAME_MAP.put("geneSeparateFlag", "是否基因分离");
        FIELD_NAME_MAP.put("transFlag", "是否转基因");
        FIELD_NAME_MAP.put(FIELD_VECTOR_TASK_CODE, "实施方案编号");
        FIELD_NAME_MAP.put("experimentNum", "试验方案编号");
        FIELD_NAME_MAP.put(FIELD_PROJECT_CODE, "项目编号");
        FIELD_NAME_MAP.put("pdImplementCode", "PD实施编号");
        FIELD_NAME_MAP.put("spotCheckResult", "抽检结果");
    }

    private static boolean isTableField(Field field) {
        TableField tableField = field.getAnnotation(TableField.class);
        return tableField == null || tableField.exist();
    }

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        List<SeedModifyTaskDTO> seedModifyTaskDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskForm(), SeedModifyTaskDTO.class);
        validateModifyTask(seedModifyTaskDTOList);
    }

    private void validateModifyTask(List<SeedModifyTaskDTO> seedModifyTaskDTOList) {
        for (SeedModifyTaskDTO seedModifyTaskDTO : seedModifyTaskDTOList) {
            if (StringUtils.isEmpty(seedModifyTaskDTO.getSeedNum())) {
                throw new BusinessException("种子编号不能为空");
            }
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedModifyTaskDTO.getSeedNum());
            if (Objects.isNull(seedStockTb)) {
                throw new BusinessException("种子编号不存在：" + seedModifyTaskDTO.getSeedNum());
            }
            List<SeedModifyTaskDTO.ModifyValueContent> modifyValueContentList = seedModifyTaskDTO.getModifyValueContentList();
            if (modifyValueContentList == null || modifyValueContentList.isEmpty()) {
                continue;
            }
            Set<String> fieldNameSet = new HashSet<>();
            for (SeedModifyTaskDTO.ModifyValueContent modifyValueContent : modifyValueContentList) {
                if (modifyValueContent == null) {
                    continue;
                }
                String fieldName = trimToNull(modifyValueContent.getKey());
                if (fieldName == null) {
                    throw new BusinessException("修改字段不能为空");
                }
                if (!fieldNameSet.add(fieldName)) {
                    throw new BusinessException("重复修改字段：" + fieldName);
                }
                if (!SEED_STOCK_TB_FIELD_SET.contains(fieldName)) {
                    throw new BusinessException("非法修改字段：" + fieldName);
                }
                Field field = SEED_STOCK_TB_FIELD_MAP.get(fieldName);
                String newFieldValue = modifyValueContent.getNewFieldValue();
                // 发起时提前校验类型转换，避免审批通过后执行失败
                try {
                    parseFieldValue(field.getType(), newFieldValue);
                } catch (Exception e) {
                    throw new BusinessException("字段值格式错误：" + fieldName + "，值：" + newFieldValue);
                }
                if (!isFieldValueChanged(seedStockTb, field, newFieldValue)) {
                    continue;
                }
                if (FIELD_PLANT_CODE.equals(fieldName)) {
                    validatePlantCodeVectorTaskCode(seedModifyTaskDTO, modifyValueContentList, newFieldValue);
                }
                if (FIELD_VECTOR_TASK_CODE.equals(fieldName) && !hasChangedField(seedStockTb, modifyValueContentList, FIELD_PLANT_CODE)) {
                    validateVectorTaskCode(seedStockTb, newFieldValue);
                }
            }

        }
    }

    private void validateVectorTaskCode(SeedStockTb seedStockTb, String vectorTaskCode) {
        vectorTaskCode = trimToNull(vectorTaskCode);
        if (StringUtils.isEmpty(vectorTaskCode)) {
            throw new BusinessException("实施方案编号不能为空");
        }
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案不存在：" + vectorTaskCode);
        }
        if (StringUtils.isEmpty(seedStockTb.getPlantCode())) {
            return;
        }
        PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(seedStockTb.getPlantCode());
        if (plantSingleStockTb == null) {
            throw new BusinessException("种植编号不存在：" + seedStockTb.getPlantCode());
        }
        if (!StringUtils.equals(vectorTaskCode, plantSingleStockTb.getVectorTaskCode())) {
            throw new BusinessException("种植编号为：" + seedStockTb.getPlantCode() + "的种子实施方案编号填写不正确");
        }
    }

    private void validatePlantCodeVectorTaskCode(SeedModifyTaskDTO seedModifyTaskDTO,
                                                 List<SeedModifyTaskDTO.ModifyValueContent> modifyValueContentList,
                                                 String plantCode) {
        plantCode = trimToNull(plantCode);
        if (StringUtils.isEmpty(plantCode)) {
            throw new BusinessException("种植编号不能为空");
        }
        String vectorTaskCode = getInputVectorTaskCode(seedModifyTaskDTO, modifyValueContentList);
        if (StringUtils.isEmpty(vectorTaskCode)) {
            throw new BusinessException("修改种植编号时实施方案编号不能为空");
        }
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案不存在：" + vectorTaskCode);
        }
        PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(plantCode);
        if (plantSingleStockTb == null) {
            throw new BusinessException("种植编号不存在：" + plantCode);
        }
        if (!StringUtils.equals(vectorTaskCode, plantSingleStockTb.getVectorTaskCode())) {
            throw new BusinessException("种植编号为：" + plantCode + "的种子实施方案编号填写不正确");
        }
    }

    private String getInputVectorTaskCode(SeedModifyTaskDTO seedModifyTaskDTO,
                                          List<SeedModifyTaskDTO.ModifyValueContent> modifyValueContentList) {
        for (SeedModifyTaskDTO.ModifyValueContent modifyValueContent : modifyValueContentList) {
            if (modifyValueContent == null) {
                continue;
            }
            String fieldName = trimToNull(modifyValueContent.getKey());
            if (FIELD_VECTOR_TASK_CODE.equals(fieldName)) {
                return trimToNull(modifyValueContent.getNewFieldValue());
            }
        }
        return trimToNull(seedModifyTaskDTO.getVectorTaskCode());
    }

    private SeedModifyTaskDTO.ModifyValueContent getModifyContent(List<SeedModifyTaskDTO.ModifyValueContent> modifyValueContentList,
                                                                  String targetFieldName) {
        for (SeedModifyTaskDTO.ModifyValueContent modifyValueContent : modifyValueContentList) {
            if (modifyValueContent == null) {
                continue;
            }
            String fieldName = trimToNull(modifyValueContent.getKey());
            if (targetFieldName.equals(fieldName)) {
                return modifyValueContent;
            }
        }
        return null;
    }

    private boolean hasChangedField(SeedStockTb seedStockTb,
                                    List<SeedModifyTaskDTO.ModifyValueContent> modifyValueContentList,
                                    String targetFieldName) {
        SeedModifyTaskDTO.ModifyValueContent modifyValueContent = getModifyContent(modifyValueContentList, targetFieldName);
        if (modifyValueContent == null) {
            return false;
        }
        Field field = SEED_STOCK_TB_FIELD_MAP.get(targetFieldName);
        return isFieldValueChanged(seedStockTb, field, modifyValueContent.getNewFieldValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (!BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            return;
        }
        List<SeedModifyTaskDTO> seedModifyTaskDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskForm(), SeedModifyTaskDTO.class);
        for (SeedModifyTaskDTO seedModifyTaskDTO : seedModifyTaskDTOList) {
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedModifyTaskDTO.getSeedNum());
            if (Objects.isNull(seedStockTb)) {
                throw new BusinessException("种子编号不存在：" + seedModifyTaskDTO.getSeedNum());
            }
            List<SeedModifyTaskDTO.ModifyValueContent> modifyValueContentList = seedModifyTaskDTO.getModifyValueContentList();
            if (modifyValueContentList == null || modifyValueContentList.isEmpty()) {
                continue;
            }
            CerVectorTaskTb modifiedVectorTaskTb = getModifiedVectorTask(seedStockTb, modifyValueContentList);
            List<SeedModifyLog> seedModifyLogList = new ArrayList<>();
            for (SeedModifyTaskDTO.ModifyValueContent modifyValueContent : modifyValueContentList) {
                if (modifyValueContent == null) {
                    continue;
                }
                String fieldName = trimToNull(modifyValueContent.getKey());
                if (modifiedVectorTaskTb != null && FIELD_PROJECT_CODE.equals(fieldName)) {
                    continue;
                }
                String newFieldValue = modifyValueContent.getNewFieldValue();
                Field field = SEED_STOCK_TB_FIELD_MAP.get(fieldName);
                if (!isFieldValueChanged(seedStockTb, field, newFieldValue)) {
                    continue;
                }
                seedModifyLogList.add(buildSeedModifyLog(seedStockTb, field, newFieldValue, bioTaskDtlTb));
                setFieldValue(seedStockTb, field, newFieldValue);
            }
            if (modifiedVectorTaskTb != null) {
                if (!StringUtils.equals(seedStockTb.getProjectCode(), modifiedVectorTaskTb.getProjectCode())) {
                    seedModifyLogList.add(buildProjectCodeModifyLog(seedStockTb, modifiedVectorTaskTb.getProjectCode(), bioTaskDtlTb));
                }
                seedStockTb.setProjectCode(modifiedVectorTaskTb.getProjectCode());
            }
            if (seedModifyLogList.isEmpty()) {
                continue;
            }
            seedStockTbMapper.updateById(seedStockTb);
            seedModifyLogMapper.insertBatch(seedModifyLogList);
        }


    }

    private CerVectorTaskTb getModifiedVectorTask(SeedStockTb seedStockTb, List<SeedModifyTaskDTO.ModifyValueContent> modifyValueContentList) {
        SeedModifyTaskDTO.ModifyValueContent modifyValueContent = getModifyContent(modifyValueContentList, FIELD_VECTOR_TASK_CODE);
        if (modifyValueContent == null) {
            return null;
        }
        Field vectorTaskCodeField = SEED_STOCK_TB_FIELD_MAP.get(FIELD_VECTOR_TASK_CODE);
        if (!isFieldValueChanged(seedStockTb, vectorTaskCodeField, modifyValueContent.getNewFieldValue())) {
            return null;
        }
        String vectorTaskCode = trimToNull(modifyValueContent.getNewFieldValue());
        if (vectorTaskCode == null) {
            throw new BusinessException("实施方案编号不能为空");
        }
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案不存在：" + vectorTaskCode);
        }
        return cerVectorTaskTb;
    }

    private SeedModifyLog buildProjectCodeModifyLog(SeedStockTb seedStockTb, String newProjectCode, BioTaskDtlTb bioTaskDtlTb) {
        Field projectCodeField = SEED_STOCK_TB_FIELD_MAP.get(FIELD_PROJECT_CODE);
        return buildSeedModifyLog(seedStockTb, projectCodeField, newProjectCode, bioTaskDtlTb);
    }

    private SeedModifyLog buildSeedModifyLog(SeedStockTb seedStockTb, Field field, String newFieldValue, BioTaskDtlTb bioTaskDtlTb) {
        if (field == null) {
            throw new BusinessException("非法修改字段");
        }
        SeedModifyLog seedModifyLog = new SeedModifyLog();
        seedModifyLog.setSeedNum(seedStockTb.getSeedNum());
        seedModifyLog.setFieldCode(field.getName());
        seedModifyLog.setFieldName(field.getName());
        seedModifyLog.setOldFieldValue(getFieldValue(seedStockTb, field));
        seedModifyLog.setNewFieldName(newFieldValue);
        seedModifyLog.setCreateTime(new Date());
        seedModifyLog.setCreateUserId(bioTaskDtlTb.getApplyUserId());
        seedModifyLog.setCreateUserName(bioTaskDtlTb.getApplyUserName());
        return seedModifyLog;
    }

    private boolean isFieldValueChanged(SeedStockTb seedStockTb, Field field, String newFieldValue) {
        if (field == null) {
            throw new BusinessException("非法修改字段");
        }
        try {
            Object oldValue = field.get(seedStockTb);
            Object parsedNewValue = parseFieldValue(field.getType(), newFieldValue);
            if (oldValue instanceof BigDecimal && parsedNewValue instanceof BigDecimal) {
                return ((BigDecimal) oldValue).compareTo((BigDecimal) parsedNewValue) != 0;
            }
            if (oldValue instanceof Date && parsedNewValue instanceof Date) {
                return ((Date) oldValue).getTime() != ((Date) parsedNewValue).getTime();
            }
            return !Objects.equals(oldValue, parsedNewValue);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("字段值比较失败：" + field.getName() + "，值：" + newFieldValue);
        }
    }

    private String getFieldValue(SeedStockTb seedStockTb, Field field) {
        try {
            Object value = field.get(seedStockTb);
            return formatFieldValue(value);
        } catch (Exception e) {
            throw new BusinessException("读取字段旧值失败：" + field.getName());
        }
    }

    private String getNewFieldValue(Field field, String value) {
        return formatFieldValue(parseFieldValue(field.getType(), value));
    }

    private String formatFieldValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Date) {
            return DateUtil.formatDateTime((Date) value);
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).stripTrailingZeros().toPlainString();
        }
        return String.valueOf(value);
    }

    private String getFieldDisplayName(String fieldCode) {
        return FIELD_NAME_MAP.getOrDefault(fieldCode, fieldCode);
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    private void setFieldValue(SeedStockTb seedStockTb, Field field, String newFieldValue) {
        if (field == null) {
            throw new BusinessException("非法修改字段");
        }
        try {
            field.set(seedStockTb, parseFieldValue(field.getType(), newFieldValue));
        } catch (Exception e) {
            throw new BusinessException("字段赋值失败：" + field.getName() + "，值：" + newFieldValue);
        }
    }

    private String trimToNull(String value) {
        if (value == null || "".equals(value.trim())) {
            return null;
        }
        return value.trim();
    }

    private Object parseFieldValue(Class<?> fieldType, String value) {
        if (String.class.equals(fieldType)) {
            return value;
        }
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
            return "true".equalsIgnoreCase(value) || "1".equals(value);
        }
        if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
            return Integer.valueOf(value);
        }
        if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
            return Long.valueOf(value);
        }
        if (BigDecimal.class.equals(fieldType)) {
            return new BigDecimal(value);
        }
        if (Date.class.equals(fieldType)) {
            return DateUtil.parse(value);
        }
        return value;
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        List<SeedModifyTaskDTO> seedModifyTaskDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskForm(), SeedModifyTaskDTO.class);
        if (seedModifyTaskDTOList == null || seedModifyTaskDTOList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        for (SeedModifyTaskDTO seedModifyTaskDTO : seedModifyTaskDTOList) {
            if (seedModifyTaskDTO == null || StringUtils.isEmpty(seedModifyTaskDTO.getSeedNum())) {
                continue;
            }
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedModifyTaskDTO.getSeedNum());
            if (seedStockTb == null) {
                continue;
            }
            List<SeedModifyTaskDTO.ModifyValueContent> modifyValueContentList = seedModifyTaskDTO.getModifyValueContentList();
            if (modifyValueContentList == null || modifyValueContentList.isEmpty()) {
                continue;
            }
            CerVectorTaskTb modifiedVectorTaskTb = getModifiedVectorTask(seedStockTb, modifyValueContentList);
            for (SeedModifyTaskDTO.ModifyValueContent modifyValueContent : modifyValueContentList) {
                if (modifyValueContent == null) {
                    continue;
                }
                String fieldName = trimToNull(modifyValueContent.getKey());
                if (fieldName == null) {
                    continue;
                }
                if (modifiedVectorTaskTb != null && FIELD_PROJECT_CODE.equals(fieldName)) {
                    continue;
                }
                Field field = SEED_STOCK_TB_FIELD_MAP.get(fieldName);
                if (!isFieldValueChanged(seedStockTb, field, modifyValueContent.getNewFieldValue())) {
                    continue;
                }
                rows.add(buildPrintRow(seedStockTb, field, modifyValueContent.getNewFieldValue()));
            }
            if (modifiedVectorTaskTb != null && !StringUtils.equals(seedStockTb.getProjectCode(), modifiedVectorTaskTb.getProjectCode())) {
                rows.add(buildPrintRow(seedStockTb, SEED_STOCK_TB_FIELD_MAP.get(FIELD_PROJECT_CODE), modifiedVectorTaskTb.getProjectCode()));
            }
        }

        if (rows.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> headers = Arrays.asList("种子编号", "修改字段", "字段编码", "旧值", "新值");
        return Collections.singletonList(buildTableSection("种子更改明细（条数：" + rows.size() + "）", headers, rows));
    }

    private Map<String, Object> buildPrintRow(SeedStockTb seedStockTb, Field field, String newFieldValue) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("种子编号", seedStockTb.getSeedNum());
        row.put("修改字段", getFieldDisplayName(field.getName()));
        row.put("字段编码", field.getName());
        row.put("旧值", getFieldValue(seedStockTb, field));
        row.put("新值", getNewFieldValue(field, newFieldValue));
        return row;
    }
}
