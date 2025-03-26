package com.bio.drqi.applet.contant;

import com.bio.drqi.applet.service.codescan.template.*;

import java.util.HashMap;
import java.util.Map;

public class ScanCodeConstant {

    public static final Map<String, Class> scanCodeClassMap=new HashMap<>();

    static {
        scanCodeClassMap.put("transform_label_print", TransformCodeScanService.class);
        scanCodeClassMap.put("vector_label_print", PlasmidCodeScanService.class);
        scanCodeClassMap.put("sample_small_label_print", SampleTestCodeScanService.class);
        scanCodeClassMap.put("sample_large_label_print", SampleTestCodeScanService.class);
        scanCodeClassMap.put("seed_in_label_print", SeedCodeScanService.class);
        scanCodeClassMap.put("seed_out_label_print", SeedCodeScanService.class);
        scanCodeClassMap.put("layout_number_label_print", NineSixLayoutCodeScanService.class);
        scanCodeClassMap.put("plant_label_print",T0PlantCodeScanService.class);
        scanCodeClassMap.put("sample_trans_print",SampleTransCodeScanService.class);
        scanCodeClassMap.put("transform_trans_print",transformTransCodeScanService.class);
        scanCodeClassMap.put("tissue_embryo_label_print",TissueEmbryoCodeScanService.class);
        scanCodeClassMap.put("bms_label_print",BmsCodeScanService.class);

    }

}
