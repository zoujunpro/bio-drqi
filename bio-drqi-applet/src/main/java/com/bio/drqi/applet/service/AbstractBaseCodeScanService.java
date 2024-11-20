package com.bio.drqi.applet.service;


import com.bio.drqi.applet.service.codescan.BaseCodeScanService;

public abstract class AbstractBaseCodeScanService<T, V> implements BaseCodeScanService {

    public abstract T parseUniqueCode(String uniqueCode);

    public  abstract V dealCodeContent(T t);
    public  V doScan(String uniqueCode) {

        T t =parseUniqueCode(uniqueCode);

        return dealCodeContent(t);
    }

}
