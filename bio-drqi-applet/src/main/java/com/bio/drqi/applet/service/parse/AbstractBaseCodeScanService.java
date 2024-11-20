package com.bio.drqi.applet.service.parse;


public abstract class AbstractBaseCodeScanService<T, V> implements BaseCodeScanService {

    abstract T parseUniqueCode(String uniqueCode);

    abstract V dealCodeContent(T t);
    public V doScan(String uniqueCode) {

        T t =parseUniqueCode(uniqueCode);

        return dealCodeContent(t);
    }

}
