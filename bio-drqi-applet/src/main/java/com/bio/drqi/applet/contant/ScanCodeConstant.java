package com.bio.drqi.applet.contant;

import com.bio.drqi.applet.service.codescan.template.*;
import com.bio.drqi.common.enums.PrintTypeEnum;

import java.util.HashMap;
import java.util.Map;

public class ScanCodeConstant {

    public static final Map<String, Class> scanCodeClassMap=new HashMap<>();

    static {
        //转化标签
        scanCodeClassMap.put(PrintTypeEnum.transform_label_print.name() , TransformCodeScanService.class);

        //转化移苗标签
        scanCodeClassMap.put(PrintTypeEnum.transform_trans_print.name() ,transformTransCodeScanService.class);

        //载体构建标签
        scanCodeClassMap.put(PrintTypeEnum.vector_label_print.name(), PlasmidCodeScanService.class);

        //取样标签-小（项目）
        scanCodeClassMap.put(PrintTypeEnum.sample_label_small_project_print.name() , SampleTestCodeScanService.class);

        //取样标签-小（大）
        scanCodeClassMap.put(PrintTypeEnum.sample_label_large_project_print.name() , SampleTestCodeScanService.class);

        //种子入库标签
        scanCodeClassMap.put(PrintTypeEnum.seed_in_label_print.name() , SeedCodeScanService.class);

        //种子出库标签
        scanCodeClassMap.put(PrintTypeEnum.seed_out_label_print.name() , SeedCodeScanService.class);

        //96版标
        scanCodeClassMap.put(PrintTypeEnum.layout_number_label_print.name(), NineSixLayoutCodeScanService.class);

        //种植标签（项目）
        scanCodeClassMap.put(PrintTypeEnum.plant_label_project_print.name(), PlantCodeScanService.class);

        //耗材标签
        scanCodeClassMap.put(PrintTypeEnum.bms_label_print.name(),BmsCodeScanService.class);

        //种植标签(CER)
        scanCodeClassMap.put(PrintTypeEnum.plant_label_cer_print.name(),PlantCodeScanService.class);

        //取样标签小（CER）
        scanCodeClassMap.put(PrintTypeEnum.sample_label_small_cer_print.name() ,SampleTestCodeScanService.class);

        //取样标签大（CER）
        scanCodeClassMap.put(PrintTypeEnum.sample_label_large_cer_print.name(),SampleTestCodeScanService.class);

        //种植申请标签(CER)
        scanCodeClassMap.put(PrintTypeEnum.plant_apply_label_print.name(),PlantApplyCodeScanService.class);

        //试验申请标签(田测)
        scanCodeClassMap.put(PrintTypeEnum.tc_experiment_label_print.name(),TcExperimentCodeScanService.class);


















    }

}
