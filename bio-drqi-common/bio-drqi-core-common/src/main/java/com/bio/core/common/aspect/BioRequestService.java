package com.bio.core.common.aspect;

import org.springframework.stereotype.Service;

@Service
public interface BioRequestService {

    void  logRequest(String requestParam,String requestMethod,String requestId,String requestDesc);
}
