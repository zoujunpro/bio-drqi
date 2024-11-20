package com.bio.drqi.applet.contant;

import com.bio.drqi.applet.service.parse.PlasmidCodeScanService;
import com.bio.drqi.applet.service.parse.SampleTestCodeScanService;
import com.bio.drqi.applet.service.parse.TransformCodeScanService;

import java.util.HashMap;
import java.util.Map;

public class ScanCodeConstant {

    public static final Map<String, Class> scanCodeClassMap=new HashMap<>();

    static {
        scanCodeClassMap.put("transform_label_print", TransformCodeScanService.class);
        scanCodeClassMap.put("vector_label_print", PlasmidCodeScanService.class);
        scanCodeClassMap.put("sample_small_label_print", SampleTestCodeScanService.class);
        scanCodeClassMap.put("sample_large_label_print", SampleTestCodeScanService.class);
    }

}
