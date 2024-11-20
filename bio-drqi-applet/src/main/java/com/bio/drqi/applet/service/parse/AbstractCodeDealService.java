package com.bio.drqi.applet.service.parse;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.SpringUtils;
import com.bio.print.api.PrintApi;
import com.bio.print.rsp.PrintDataRspDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


public abstract class AbstractCodeDealService<T, V> implements BaseCodeParse {

    abstract T parseUniqueCode(String uniqueCode);

    abstract V dealCodeContent(T t);
    public V doScan(String uniqueCode) {

        T t =parseUniqueCode(uniqueCode);

        return dealCodeContent(t);
    }

}
