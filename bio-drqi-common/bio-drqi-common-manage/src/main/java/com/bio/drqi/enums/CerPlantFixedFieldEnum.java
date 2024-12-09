package com.bio.drqi.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CerPlantFixedFieldEnum {
    sampleCode("sampleCode", "取样编号"),
    checkResult("checkResult", "材料下一步安排"),
    editType("editType", "编辑类型"),
    acceptorMaterial("acceptorMaterial", "作物（受体材料）"),
    species("species", "品种"),
    generation("generation", "代次"),
    plantStatus("plantStatus", "植株状态"),
    plantNumber("plantNumber", "株数"),
    plantDate("plantDate", "播种/移苗日期"),
    transplantDate("transplantDate", "移栽日期"),
    vernalizationBeginDate("vernalizationBeginDate", "春化开始日期"),
    vernalizationEndDate("vernalizationEndDate", "春化结束日期"),
    pollinationMethod("pollinationMethod", "授粉方式"),
    fatherInfo("fatherInfo", "父本信息"),
    motherInfo("motherInfo", "母本信息"),
    pollinationDate("pollinationDate", "授粉时间"),
    harvestDate("harvestDate", "收获时间"),
    ;
    public String fieldEName;
    public String fieldCName;

    CerPlantFixedFieldEnum(String fieldEName, String fieldCName) {
        this.fieldEName = fieldEName;
        this.fieldCName = fieldCName;
    }

    public static List<Map<String, String>> getFixedField() {
        List<Map<String, String>> CER_FIXED_FIELD = new ArrayList<>();
        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.harvestDate.fieldEName, CerPlantFixedFieldEnum.harvestDate.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.pollinationDate.fieldEName, CerPlantFixedFieldEnum.pollinationDate.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.motherInfo.fieldEName, CerPlantFixedFieldEnum.motherInfo.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.fatherInfo.fieldEName, CerPlantFixedFieldEnum.fatherInfo.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.pollinationMethod.fieldEName, CerPlantFixedFieldEnum.pollinationMethod.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.vernalizationEndDate.fieldEName, CerPlantFixedFieldEnum.vernalizationEndDate.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.vernalizationBeginDate.fieldEName, CerPlantFixedFieldEnum.vernalizationBeginDate.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.transplantDate.fieldEName, CerPlantFixedFieldEnum.transplantDate.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.plantDate.fieldEName, CerPlantFixedFieldEnum.plantDate.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.plantNumber.fieldEName, CerPlantFixedFieldEnum.plantNumber.fieldCName);
        }});

        return CER_FIXED_FIELD;
    }

    public static List<Map<String, String>> getAllFixedField() {
        List<Map<String, String>> CER_FIXED_FIELD = new ArrayList<>();
        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.sampleCode.fieldEName, CerPlantFixedFieldEnum.sampleCode.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.checkResult.fieldEName, CerPlantFixedFieldEnum.checkResult.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.editType.fieldEName, CerPlantFixedFieldEnum.editType.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.acceptorMaterial.fieldEName, CerPlantFixedFieldEnum.acceptorMaterial.fieldCName);
        }});
        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.species.fieldEName, CerPlantFixedFieldEnum.species.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.generation.fieldEName, CerPlantFixedFieldEnum.generation.fieldCName);
        }});
        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.plantNumber.fieldEName, CerPlantFixedFieldEnum.plantNumber.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.plantDate.fieldEName, CerPlantFixedFieldEnum.plantDate.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.transplantDate.fieldEName, CerPlantFixedFieldEnum.transplantDate.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.vernalizationBeginDate.fieldEName, CerPlantFixedFieldEnum.vernalizationBeginDate.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.vernalizationEndDate.fieldEName, CerPlantFixedFieldEnum.vernalizationEndDate.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.pollinationMethod.fieldEName, CerPlantFixedFieldEnum.pollinationMethod.fieldCName);
        }});

        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.plantStatus.fieldEName, CerPlantFixedFieldEnum.plantStatus.fieldCName);
        }});
        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.fatherInfo.fieldEName, CerPlantFixedFieldEnum.fatherInfo.fieldCName);
        }});
        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.motherInfo.fieldEName, CerPlantFixedFieldEnum.motherInfo.fieldCName);
        }});
        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.pollinationDate.fieldEName, CerPlantFixedFieldEnum.pollinationDate.fieldCName);
        }});
        CER_FIXED_FIELD.add(new HashMap<String, String>() {{
            put(CerPlantFixedFieldEnum.harvestDate.fieldEName, CerPlantFixedFieldEnum.harvestDate.fieldCName);
        }});
        return CER_FIXED_FIELD;
    }


}
