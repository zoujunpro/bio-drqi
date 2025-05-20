package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.drqi.domain.TcExperimentDesignTb;
import lombok.Data;

@Data
public class TcPollinationExcelDTO {

    /**
     * 母本小区编号
     */
    @ExcelProperty(value = "小区编号",index = 2)
    private String motherRegionNum;
    /**
     * 母本种子编号
     */
    @ExcelProperty(value = "种子编号",index = 2)
    private String motherSeedNum;
    /**
     * 母本单株编号
     */
    @ExcelProperty(value ="单株编号(母本)",index = 2)
    private String motherSampleCode;
    /**
     * 母本品种
     */
    @ExcelProperty(value ="品种(母本)",index = 2)
    private String motherBreedName;
    /**
     * 母本实施方案编号
     */
    @ExcelProperty(value ="实施方案编号(母本)",index = 2)
    private String motherVectorTaskCode;
    /**
     * 母本世代
     */
    @ExcelProperty(value ="世代(母本)",index = 2)
    private String motherGenerationName;
    /**
     * 母本基因类型
     */
    @ExcelProperty(value ="基因型(母本)",index = 2)
    private String motherTcGene;
    /**
     * 父本小区编号
     */
    @ExcelProperty(value ="小区编号(父本)",index = 2)
    private String fatherRegionNum;
    /**
     * 父本种子编号
     */
    @ExcelProperty(value ="种子编号(父本)",index = 2)
    private String fatherSeedNum;

    /**
     * 父本单株编号
     */
    @ExcelProperty(value ="单株编号(父本)",index = 2)
    private String fatherSampleCode;
    /**
     * 父本品种
     */
    @ExcelProperty(value ="品种(父本)",index = 2)
    private String fatherBreedName;

    /**
     * 父本实施方案编号
     */
    @ExcelProperty(value ="实施方案编号(父本)",index = 2)
    private String fatherVectorTaskCode;
    /**
     * 父本世代
     */
    @ExcelProperty(value ="世代(父本)",index = 2)
    private String fatherGenerationName;
    /**
     * 父本基因类型
     */
    @ExcelProperty(value ="基因型(父本)",index = 2)
    private String fatherTcGene;

    /**
     * 授粉时间
     */
    @ExcelProperty(value ="授粉时间",index = 1)
    private String pollinationDate;

    /**
     * 授粉方式名称
     */
    @ExcelProperty(value ="授粉方式",index = 1)
    private String pollinationMethodName;
    /**
     * 收获方式名称
     */
    @ExcelProperty(value ="收获方式",index = 1)
    private String harvestTypeName;
    /**
     * 备注
     */
    @ExcelProperty(value ="备注",index = 1)
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
