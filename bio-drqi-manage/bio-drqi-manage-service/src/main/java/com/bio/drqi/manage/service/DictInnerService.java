package com.bio.drqi.manage.service;

import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.domain.BioDict;

public interface DictInnerService {

    BioDict findByDictTypeAndDictValueCode(BioDictTypeEnum bioDictTypeEnum, String dictValueCode);


    BioDict findByDictTypeAndDictValueName(BioDictTypeEnum bioDictTypeEnum, String dictValueName);
}
