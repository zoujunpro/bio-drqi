package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.TcExperimentDesignTb;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcPollinationExcelDTO {

    /**
     * 母本小区编号
     */
    @ExcelProperty(value = {"母本","小区编号(母)"})
    @NotBlank(message = "参数缺失：小区编号(母)")
    private String motherRegionNum;
    /**
     * 母本种子编号
     */
    @ExcelProperty(value = {"母本","种子编号(母)"})
    @NotBlank(message = "参数缺失：种子编号(母)")
    private String motherSeedNum;
    /**
     * 母本单株编号
     */
    @ExcelProperty(value ={"母本","单株编号(母)"})
    @NotBlank(message = "参数缺失：单株编号(母)")
    private String motherSampleCode;


    /**
     * 母本品种
     */
    @ExcelProperty(value ={"母本","品种(母)"})
    @NotBlank(message = "参数缺失：品种(母)")
    private String motherBreedName;

    private String motherBreedCode;
    /**
     * 母本实施方案编号
     */
    @ExcelProperty(value ={"母本","实施方案编号(母)"})
    @NotBlank(message = "参数缺失：实施方案编号(母)")
    private String motherVectorTaskCode;
    /**
     * 母本世代
     */
    @ExcelProperty(value ={"母本","世代(母)"})
    @NotBlank(message = "参数缺失：世代(母)")
    private String motherGenerationName;
    /**
     * 母本基因类型
     */
    @ExcelProperty(value ={"母本","基因型(母)"})
    @NotBlank(message = "参数缺失：基因型(母)")
    private String motherTcGene;
    /**
     * 父本小区编号
     */
    @ExcelProperty(value ={"父本","小区编号(父)"})
    @NotBlank(message = "参数缺失：小区编号(父)")
    private String fatherRegionNum;
    /**
     * 父本种子编号
     */
    @ExcelProperty(value ={"父本","种子编号(父)"})
    @NotBlank(message = "参数缺失：种子编号(父)")
    private String fatherSeedNum;

    /**
     * 父本单株编号
     */
    @ExcelProperty(value ={"父本","单株编号(父)"})
    @NotBlank(message = "参数缺失：单株编号(父)")
    private String fatherSampleCode;
    /**
     * 父本品种
     */
    @ExcelProperty(value ={"父本","品种(父)"})
    @NotBlank(message = "参数缺失：品种(父)")
    private String fatherBreedName;

    private String fatherBreedCode;

    /**
     * 父本实施方案编号
     */
    @ExcelProperty(value ={"父本","实施方案编号(父)"})
    @NotBlank(message = "参数缺失：实施方案编号(父)")
    private String fatherVectorTaskCode;
    /**
     * 父本世代
     */
    @ExcelProperty(value ={"父本","世代(父)"})
    @NotBlank(message = "参数缺失：世代(父)")
    private String fatherGenerationName;
    /**
     * 父本基因类型
     */
    @ExcelProperty(value ={"基因型(父)"})
    @NotBlank(message = "参数缺失：基因型(父)")
    private String fatherTcGene;

    /**
     * 授粉时间
     */
    @ExcelProperty(value ={"授粉信息","授粉时间"})
    @NotBlank(message = "参数缺失：授粉时间")
    private String pollinationDate;

    /**
     * 收获方式名称
     */
    @ExcelProperty(value ={"授粉信息","收获方式"})
    @NotBlank(message = "参数缺失：收获方式")
    private String harvestTypeName;

    @ExcelIgnore
    private String harvestTypeCode;

    /**
     * 备注
     */
    @ExcelProperty(value ={"授粉信息","备注"})
    private String remark;


    public static TcPollinationExcelDTO ofMather(TcExperimentDesignTb tcExperimentDesignTb, String sample,String breedName) {
        TcPollinationExcelDTO tcPollinationExcelDTO = new TcPollinationExcelDTO();
        tcPollinationExcelDTO.setMotherRegionNum(tcExperimentDesignTb.getRegionNum());
        tcPollinationExcelDTO.setMotherSeedNum(tcExperimentDesignTb.getSeedNum());
        tcPollinationExcelDTO.setMotherSampleCode(sample);
        tcPollinationExcelDTO.setMotherBreedName(breedName);
        tcPollinationExcelDTO.setMotherVectorTaskCode(tcExperimentDesignTb.getVectorTaskCode());
        tcPollinationExcelDTO.setMotherGenerationName(tcExperimentDesignTb.getGenerationCode());
        tcPollinationExcelDTO.setMotherTcGene(tcExperimentDesignTb.getTcGene());
        return tcPollinationExcelDTO;
    }

    public static TcPollinationExcelDTO ofFather(TcExperimentDesignTb tcExperimentDesignTb, String sample,String breedName) {
        TcPollinationExcelDTO tcPollinationExcelDTO = new TcPollinationExcelDTO();
        tcPollinationExcelDTO.setFatherRegionNum(tcExperimentDesignTb.getRegionNum());
        tcPollinationExcelDTO.setFatherSeedNum(tcExperimentDesignTb.getSeedNum());
        tcPollinationExcelDTO.setFatherSampleCode(null);
        tcPollinationExcelDTO.setFatherBreedName(breedName);
        tcPollinationExcelDTO.setFatherVectorTaskCode(tcExperimentDesignTb.getVectorTaskCode());
        tcPollinationExcelDTO.setFatherGenerationName(tcExperimentDesignTb.getGenerationCode());
        tcPollinationExcelDTO.setFatherTcGene(tcExperimentDesignTb.getTcGene());
        tcPollinationExcelDTO.setFatherSampleCode(sample);
        return tcPollinationExcelDTO;
    }


}
