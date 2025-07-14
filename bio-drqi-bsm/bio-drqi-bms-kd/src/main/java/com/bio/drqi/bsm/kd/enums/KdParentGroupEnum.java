package com.bio.drqi.bsm.kd.enums;

import com.bio.common.core.dto.BusinessException;

public enum KdParentGroupEnum {
    CODE_1("2536291","试剂耗材"),
    CODE_2("2536292","技术服务"),
    CODE_3("2536293","设备维修"),
    CODE_4("2536294","危废处置"),

    ;
    public String code;
    public String name;

    KdParentGroupEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static KdParentGroupEnum ofCode(String code){
      for (KdParentGroupEnum kdParentGroupEnum:KdParentGroupEnum.values()){
          if(kdParentGroupEnum.code.equals(code)){
              return kdParentGroupEnum;
          }
      }
      throw new BusinessException("材料分组未配置父级分组");
    }
}
