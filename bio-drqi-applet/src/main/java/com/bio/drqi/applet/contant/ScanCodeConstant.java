package com.bio.drqi.applet.contant;

import com.bio.drqi.applet.service.codescan.template.*;

import java.util.HashMap;
import java.util.Map;

public class ScanCodeConstant {

    public static final Map<String, Class> scanCodeClassMap=new HashMap<>();

    static {
        scanCodeClassMap.put("transform_label_print", TransformCodeScanService.class);
        scanCodeClassMap.put("transform_trans_print",transformTransCodeScanService.class);

        scanCodeClassMap.put("vector_label_print", PlasmidCodeScanService.class);
        scanCodeClassMap.put("sample_label_small_project_print", ProjectSampleTestCodeScanService.class);
        scanCodeClassMap.put("sample_label_large_project_print", ProjectSampleTestCodeScanService.class);
        scanCodeClassMap.put("seed_in_label_print", SeedCodeScanService.class);
        scanCodeClassMap.put("seed_out_label_print", SeedCodeScanService.class);
        scanCodeClassMap.put("layout_number_label_print", NineSixLayoutCodeScanService.class);
        scanCodeClassMap.put("plant_label_project_print", ProjectPlantCodeScanService.class);
        scanCodeClassMap.put("tissue_embryo_label_print",TissueEmbryoCodeScanService.class);
        scanCodeClassMap.put("bms_label_print",BmsCodeScanService.class);

    }

}
