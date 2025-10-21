package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.TcExperimentDesignTb;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
public class TcPollinationExcelDTO {

    /**
     * 母本小区编号
     */
    @ExcelProperty(value = {"母本","母本小区编号"})
    @NotBlank(message = "参数缺失：母本小区编号")
    private String motherRegionNum;
    /**
     * 母本种子编号
     */
    @ExcelProperty(value = {"母本","母本种子编号"})
    @NotBlank(message = "参数缺失：母本种子编号")
    private String motherSeedNum;
    /**
     * 母本单株编号
     */
    @ExcelProperty(value ={"母本","母本分子取样编号"})
    private String motherSampleCode;

    /**
     * 母本大田取样编号
     */
    @ExcelProperty(value ={"母本","母本大田取样编号"})
    private String motherTcSampleCode;

    /**
     * 母本单株编号
     */
    @ExcelProperty(value ={"母本","母本单株编号"})
    @NotBlank(message = "参数缺失：母本单株编号")
    private String motherSingleNumber;

    /**
     * 母本品种
     */
    @ExcelProperty(value ={"母本","母本品种"})
    @NotBlank(message = "参数缺失：母本品种")
    private String motherBreedName;

    private String motherBreedCode;
    /**
     * 母本实施方案编号
     */
    @ExcelProperty(value ={"母本","母本实施方案编号"})
    @NotBlank(message = "参数缺失：母本实施方案编号")
    private String motherVectorTaskCode;
    /**
     * 母本世代
     */
    @ExcelProperty(value ={"母本","母本世代"})
    @NotBlank(message = "参数缺失：母本世代")
    private String motherGenerationName;
    /**
     * 母本基因类型
     */
    @ExcelProperty(value ={"母本","母本基因型"})
    @NotBlank(message = "参数缺失：母本基因型")
    private String motherTcGene;
    /**
     * 父本小区编号
     */
    @ExcelProperty(value ={"父本","父本小区编号"})
    @NotBlank(message = "参数缺失：父本小区编号")
    private String fatherRegionNum;
    /**
     * 父本种子编号
     */
    @ExcelProperty(value ={"父本","父本种子编号"})
    @NotBlank(message = "参数缺失：父本种子编号")
    private String fatherSeedNum;

    /**
     * 父本大田单株编号
     */
    @ExcelProperty(value ={"父本","父本大田取样编号"})
    private String fatherTcSampleCode;

    /**
     * 父本单株编号
     */
    @ExcelProperty(value ={"父本","父本分子取样编号"})
    private String fatherSampleCode;


    /**
     * 父本大田单株编号
     */
    @ExcelProperty(value ={"父本","父本单株编号"})
    @NotBlank(message = "参数缺失：父本单株编号")
    private String fatherSingleNumber;
    /**
     * 父本品种
     */
    @ExcelProperty(value ={"父本","父本品种"})
    @NotBlank(message = "参数缺失：父本品种")
    private String fatherBreedName;

    private String fatherBreedCode;

    /**
     * 父本实施方案编号
     */
    @ExcelProperty(value ={"父本","父本实施方案编号"})
    @NotBlank(message = "参数缺失：父本实施方案编号")
    private String fatherVectorTaskCode;
    /**
     * 父本世代
     */
    @ExcelProperty(value ={"父本","父本世代"})
    @NotBlank(message = "参数缺失：父本世代")
    private String fatherGenerationName;
    /**
     * 父本基因类型
     */
    @ExcelProperty(value ={"父本基因型"})
    @NotBlank(message = "参数缺失：父本基因型")
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


    public static TcPollinationExcelDTO ofMother(TcExperimentDesignTb tcExperimentDesignTb, String sampleCode,String tcSampleCode,String motherSingleNumber,String breedName) {
        TcPollinationExcelDTO tcPollinationExcelDTO = new TcPollinationExcelDTO();
        tcPollinationExcelDTO.setMotherRegionNum(tcExperimentDesignTb.getRegionNum());
        tcPollinationExcelDTO.setMotherSeedNum(tcExperimentDesignTb.getSeedNum());
        tcPollinationExcelDTO.setMotherSampleCode(sampleCode);
        tcPollinationExcelDTO.setMotherSingleNumber(motherSingleNumber);
        tcPollinationExcelDTO.setMotherBreedName(breedName);
        tcPollinationExcelDTO.setMotherVectorTaskCode(tcExperimentDesignTb.getVectorTaskCode());
        tcPollinationExcelDTO.setMotherGenerationName(tcExperimentDesignTb.getGenerationCode());
        tcPollinationExcelDTO.setMotherTcGene(tcExperimentDesignTb.getTcGene());
        tcPollinationExcelDTO.setMotherTcSampleCode(tcSampleCode);
        return tcPollinationExcelDTO;
    }

    public static TcPollinationExcelDTO ofFather(TcExperimentDesignTb tcExperimentDesignTb, String sampleCode,String tcSampleCode,String fatherSingleNumber,String breedName) {
        TcPollinationExcelDTO tcPollinationExcelDTO = new TcPollinationExcelDTO();
        tcPollinationExcelDTO.setFatherRegionNum(tcExperimentDesignTb.getRegionNum());
        tcPollinationExcelDTO.setFatherSeedNum(tcExperimentDesignTb.getSeedNum());
        tcPollinationExcelDTO.setFatherBreedName(breedName);
        tcPollinationExcelDTO.setFatherSingleNumber(fatherSingleNumber);
        tcPollinationExcelDTO.setFatherVectorTaskCode(tcExperimentDesignTb.getVectorTaskCode());
        tcPollinationExcelDTO.setFatherGenerationName(tcExperimentDesignTb.getGenerationCode());
        tcPollinationExcelDTO.setFatherTcGene(tcExperimentDesignTb.getTcGene());
        tcPollinationExcelDTO.setFatherSampleCode(sampleCode);
        tcPollinationExcelDTO.setFatherTcSampleCode(tcSampleCode);

        return tcPollinationExcelDTO;
    }


}
