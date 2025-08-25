package com.bio.drqi.bsm.kd.enums;

import com.bio.common.core.dto.BusinessException;

public enum KdParentGroupEnum {
    DEV_CODE_1("2536291","试剂耗材","SJHC","dev"),
    DEV_CODE_2("2536292","技术服务","JSFW","dev"),
    DEV_CODE_3("2536293","设备维修","SBWX","dev"),
    DEV_CODE_4("2536294","危废处置","WFCZ","dev"),


    TEST_CODE_1("2536291","试剂耗材","SJHC","test"),
    TEST_CODE_2("2536292","技术服务","JSFW","test"),
    TEST_CODE_3("2536293","设备维修","SBWX","test"),
    TEST_CODE_4("2536294","危废处置","WFCZ","test"),


    LOCAL_CODE_1("2536291","试剂耗材","SJHC","local"),
    LOCAL_CODE_2("2536292","技术服务","JSFW","local"),
    LOCAL_CODE_3("2536293","设备维修","SBWX","local"),
    LOCAL_CODE_4("2536294","危废处置","WFCZ","local"),


    PROD_CODE_1("2536291","试剂耗材","SJHC","prod"),
    PROD_CODE_2("2536292","技术服务","JSFW","prod"),
    PROD_CODE_3("2536293","设备维修","SBWX","prod"),
    PROD_CODE_4("2536294","危废处置","WFCZ","prod"),

    ;
    public String code;
    public String name;
    public String active;
    public String type;

    KdParentGroupEnum(String code, String name,String type,String active) {
        this.code = code;
        this.name = name;
        this.active=active;
        this.type=type;
    }

    public static KdParentGroupEnum ofCode(String code,String active){
      for (KdParentGroupEnum kdParentGroupEnum:KdParentGroupEnum.values()){
          if(kdParentGroupEnum.code.equals(code)&&kdParentGroupEnum.active.equals(active)){
              return kdParentGroupEnum;
          }
      }
      throw new BusinessException("材料分组未配置父级分组");
    }
}
