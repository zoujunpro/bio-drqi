package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.drqi.domain.TcExperimentDesignTb;
import lombok.Data;

@Data
public class TcPollinationExcelDTO {

    /**
     * 母本小区编号
     */
    @ExcelProperty("小区编号(母本)")
    private String motherRegionNum;
    /**
     * 母本种子编号
     */
    @ExcelProperty("种子编号(母本)")
    private String motherSeedNum;
    /**
     * 母本单株编号
     */
    @ExcelProperty("单株编号(母本)")
    private String motherSampleCode;
    /**
     * 母本品种
     */
    @ExcelProperty("品种(母本)")
    private String motherBreedName;
    /**
     * 母本实施方案编号
     */
    @ExcelProperty("实施方案编号(母本)")
    private String motherVectorTaskCode;
    /**
     * 母本世代
     */
    @ExcelProperty("世代(母本)")
    private String motherGenerationName;
    /**
     * 母本基因类型
     */
    @ExcelProperty("基因型(母本)")
    private String motherTcGene;
    /**
     * 父本小区编号
     */
    @ExcelProperty("小区编号(父本)")
    private String fatherRegionNum;
    /**
     * 父本种子编号
     */
    @ExcelProperty("种子编号(父本)")
    private String fatherSeedNum;

    /**
     * 父本单株编号
     */
    @ExcelProperty("单株编号(父本)")
    private String fatherSampleCode;
    /**
     * 父本品种
     */
    @ExcelProperty("品种(父本)")
    private String fatherBreedName;

    /**
     * 父本实施方案编号
     */
    @ExcelProperty("实施方案编号(父本)")
    private String fatherVectorTaskCode;
    /**
     * 父本世代
     */
    @ExcelProperty("世代(父本)")
    private String fatherGenerationName;
    /**
     * 父本基因类型
     */
    @ExcelProperty("基因型(父本)")
    private String fatherTcGene;

    /**
     * 授粉时间
     */
    @ExcelProperty("授粉时间")
    private String pollinationDate;

    /**
     * 授粉方式名称
     */
    @ExcelProperty("授粉方式")
    private String pollinationMethodName;
    /**
     * 收获方式名称
     */
    @ExcelProperty("收获方式")
    private String harvestTypeName;
    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;


    public static TcPollinationExcelDTO ofMather(TcExperimentDesignTb tcExperimentDesignTb, String sample) {
        TcPollinationExcelDTO tcPollinationExcelDTO = new TcPollinationExcelDTO();
        tcPollinationExcelDTO.setMotherRegionNum(tcExperimentDesignTb.getRegionNum());
        tcPollinationExcelDTO.setMotherSeedNum(tcExperimentDesignTb.getSeedNum());
        tcPollinationExcelDTO.setMotherSampleCode(sample);
        tcPollinationExcelDTO.setMotherBreedName(tcExperimentDesignTb.getBreedName());
        tcPollinationExcelDTO.setMotherVectorTaskCode(tcExperimentDesignTb.getVectorTaskCode());
        tcPollinationExcelDTO.setMotherGenerationName(tcExperimentDesignTb.getGenerationCode());
        tcPollinationExcelDTO.setMotherTcGene(tcExperimentDesignTb.getTcGene());
        return tcPollinationExcelDTO;
    }

    public static TcPollinationExcelDTO ofFather(TcExperimentDesignTb tcExperimentDesignTb, String sample) {
        TcPollinationExcelDTO tcPollinationExcelDTO = new TcPollinationExcelDTO();
        tcPollinationExcelDTO.setFatherRegionNum(tcExperimentDesignTb.getRegionNum());
        tcPollinationExcelDTO.setFatherSeedNum(tcExperimentDesignTb.getSeedNum());
        tcPollinationExcelDTO.setFatherSampleCode(null);
        tcPollinationExcelDTO.setFatherBreedName(tcExperimentDesignTb.getBreedName());
        tcPollinationExcelDTO.setFatherVectorTaskCode(tcExperimentDesignTb.getVectorTaskCode());
        tcPollinationExcelDTO.setFatherGenerationName(tcExperimentDesignTb.getGenerationCode());
        tcPollinationExcelDTO.setFatherTcGene(tcExperimentDesignTb.getTcGene());
        tcPollinationExcelDTO.setFatherSampleCode(sample);
        return tcPollinationExcelDTO;
    }


}
