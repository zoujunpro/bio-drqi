package com.bio.drqi.manage.flowtask.seed;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.manage.dto.seed.SeedModifyTaskDTO;
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

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    static {
        Field[] fields = SeedStockTb.class.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                SEED_STOCK_TB_FIELD_SET.add(field.getName());
                field.setAccessible(true);
                SEED_STOCK_TB_FIELD_MAP.put(field.getName(), field);
            }
        }
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
            Map<String, SeedModifyTaskDTO.ModifyValueContent> modifyContentMap = seedModifyTaskDTO.getModifyContentMap();
            if (modifyContentMap == null || modifyContentMap.isEmpty()) {
                continue;
            }
            for (Map.Entry<String, SeedModifyTaskDTO.ModifyValueContent> entry : modifyContentMap.entrySet()) {
                String fieldName = entry.getKey();
                if (fieldName == null || "".equals(fieldName.trim())) {
                    throw new BusinessException("修改字段不能为空");
                }
                if (!SEED_STOCK_TB_FIELD_SET.contains(fieldName)) {
                    throw new BusinessException("非法修改字段：" + fieldName);
                }
                SeedModifyTaskDTO.ModifyValueContent modifyValueContent = entry.getValue();
                if (modifyValueContent == null) {
                    continue;
                }
                Field field = SEED_STOCK_TB_FIELD_MAP.get(fieldName);
                String newFieldValue = modifyValueContent.getNewFieldValue();
                // 发起时提前校验类型转换，避免审批通过后执行失败
                try {
                    parseFieldValue(field.getType(), newFieldValue);
                } catch (Exception e) {
                    throw new BusinessException("字段值格式错误：" + fieldName + "，值：" + newFieldValue);
                }
            }
        }
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
            Map<String, SeedModifyTaskDTO.ModifyValueContent> modifyContentMap = seedModifyTaskDTO.getModifyContentMap();
            if (modifyContentMap == null || modifyContentMap.isEmpty()) {
                continue;
            }
            for (Map.Entry<String, SeedModifyTaskDTO.ModifyValueContent> entry : modifyContentMap.entrySet()) {
                String fieldName = entry.getKey();
                SeedModifyTaskDTO.ModifyValueContent modifyValueContent = entry.getValue();
                if (modifyValueContent == null) {
                    continue;
                }
                String newFieldValue = modifyValueContent.getNewFieldValue();
                Field field = SEED_STOCK_TB_FIELD_MAP.get(fieldName);
                setFieldValue(seedStockTb, field, newFieldValue);
            }
            seedStockTbMapper.updateById(seedStockTb);
        }


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

    private Object parseFieldValue(Class<?> fieldType, String value) {
        if (String.class.equals(fieldType)) {
            return value;
        }
        if (fieldType.equals(boolean.class)) {
            return "true".equalsIgnoreCase(value) || "1".equals(value);
        }
        if (StringUtils.isEmpty(value)) {
            return null;
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
        return Collections.emptyList();
    }
}
