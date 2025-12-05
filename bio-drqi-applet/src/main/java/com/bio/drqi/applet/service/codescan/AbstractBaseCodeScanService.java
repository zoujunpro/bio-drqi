package com.bio.drqi.applet.service.codescan;


import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.service.codescan.dto.BioResultInfoDTO;
import com.bio.drqi.domain.BioSampleTestTwoResultDetailTb;
import com.bio.drqi.domain.BioSampleTestTwoResultTb;
import com.bio.drqi.mapper.BioSampleTestTwoResultDetailTbMapper;
import com.bio.drqi.mapper.BioSampleTestTwoResultTbMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractBaseCodeScanService<T, V> implements BaseCodeScanService {



    public abstract T parseUniqueCode(String uniqueCode);

    public abstract V dealCodeContent(T t);

    public V doScan(String uniqueCode) {

        T t = parseUniqueCode(uniqueCode);

        return dealCodeContent(t);
    }



}
