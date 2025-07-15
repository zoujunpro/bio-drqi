package com.bio.drqi.bsm.kd.enums;

import com.bio.common.core.dto.BusinessException;

public enum KdParentGroupEnum {
    DEV_CODE_1("2536291","试剂耗材","dev"),
    DEV_CODE_2("2536292","技术服务","dev"),
    DEV_CODE_3("2536293","设备维修","dev"),
    DEV_CODE_4("2536294","危废处置","dev"),


    TEST_CODE_1("2536291","试剂耗材","test"),
    TEST_CODE_2("2536292","技术服务","test"),
    TEST_CODE_3("2536293","设备维修","test"),
    TEST_CODE_4("2536294","危废处置","test"),


    LOCAL_CODE_1("2536291","试剂耗材","local"),
    LOCAL_CODE_2("2536292","技术服务","local"),
    LOCAL_CODE_3("2536293","设备维修","local"),
    LOCAL_CODE_4("2536294","危废处置","local"),


    PROD_CODE_1("2536291","试剂耗材","prod"),
    PROD_CODE_2("2536292","技术服务","prod"),
    PROD_CODE_3("2536293","设备维修","prod"),
    PROD_CODE_4("2536294","危废处置","prod"),

    ;
    public String code;
    public String name;
    public String active;

    KdParentGroupEnum(String code, String name,String active) {
        this.code = code;
        this.name = name;
        this.active=active;
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
