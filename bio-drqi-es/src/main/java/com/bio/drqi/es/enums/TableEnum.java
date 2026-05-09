package com.bio.drqi.es.enums;

import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;

public enum TableEnum {
    bio_task_dtl_tb(BioTaskDtlTb.class, BioTaskConfMapper.class),
    cer_project_tb(CerProjectTb.class, CerProjectTbMapper.class),
    cer_sub_project_tb(CerSubProjectTb.class, CerSubProjectTbMapper.class),
    cer_vector_task_tb(CerProjectTb.class, CerProjectTbMapper.class),
    cer_vector_tb(CerVectorTb.class, CerVectorTbMapper.class),
    cer_transform_tb(CerTransformTb.class, CerTransformTbMapper.class),
    cer_plasmid_quality_tb(CerPlasmidQualityTb.class, CerPlasmidQualityTbMapper.class),
    cer_conversion_and_trans_tb(CerConversionAndTransTb.class, CerConversionAndTransTbMapper.class),
    cer_conversion_and_trans_ref(CerConversionAndTransRef.class, CerConversionAndTransRefMapper.class),
    bio_sample_test_tb(BioSampleTestTb.class, BioSampleTestTbMapper.class),
    bio_sample_test_one_result_tb(BioSampleTestOneResultTb.class, BioSampleTestOneResultTbMapper.class),
    bio_sample_test_two_result_tb(BioSampleTestTwoResultTb.class, BioSampleTestTwoResultTbMapper.class),
    bio_sample_test_two_result_detail_tb(BioSampleTestTwoResultTb.class, BioSampleTestTwoResultTbMapper.class),
    plant_apply_tb(PlantApplyTb.class, PlantApplyTbMapper.class),
    plant_apply_detail_tb(PlantApplyDetailTb.class, PlantApplyDetailTbMapper.class),
    plant_multiple_stock_tb(PlantMultipleStockTb.class, PlantMultipleStockTbMapper.class),
    plant_single_stock_tb(PlantSingleStockTb.class, PlantSingleStockTbMapper.class),
    seed_stock_tb(SeedStockTb.class, SeedStockTbMapper.class),
    ;
    public Class mapper;
    public Class domain;

    TableEnum(Class mapper, Class domain) {
        this.mapper = mapper;
        this.domain = domain;
    }

    public static TableEnum getTableEnum(String table) {
        for (TableEnum tableEnum : TableEnum.values()) {
            if (tableEnum.name().equalsIgnoreCase(table)) {
                return tableEnum;
            }
        }
        throw new BusinessException("未配置改表的数据同步");
    }

}
