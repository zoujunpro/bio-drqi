package com.bio.drqi.applet.contant;

import com.bio.drqi.applet.service.parse.PlasmidCodeParseService;
import com.bio.drqi.applet.service.parse.SampleTestCodeParseService;
import com.bio.drqi.applet.service.parse.TransformCodeParseService;

import java.util.HashMap;
import java.util.Map;

public class ScanCodeConstant {

    public static final Map<String, Class> scanCodeClassMap=new HashMap<>();

    static {
        scanCodeClassMap.put("transform_label_print", TransformCodeParseService.class);
        scanCodeClassMap.put("vector_label_print", PlasmidCodeParseService.class);
        scanCodeClassMap.put("sample_small_label_print", SampleTestCodeParseService.class);
        scanCodeClassMap.put("sample_large_label_print", SampleTestCodeParseService.class);
    }

}
