package com.bio.drqi.manage.service;

import com.bio.common.core.context.SecurityContextHolder;
import com.bio.core.common.aspect.BioRequestService;
import com.bio.drqi.domain.BioRequestLog;
import com.bio.drqi.mapper.BioRequestLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class BioRequestServiceImpl implements BioRequestService {

    @Resource
    private BioRequestLogMapper bioRequestLogMapper;

    @Override
    public void logRequest(String requestParam, String requestMethod, String requestId,String requestDesc) {

        try {
            BioRequestLog bioRequestLog = new BioRequestLog();
            bioRequestLog.setRequestUserId(SecurityContextHolder.getUserId());
            bioRequestLog.setRequestUserName(SecurityContextHolder.getNickName());
            bioRequestLog.setRequestTime(new Date());
            bioRequestLog.setRequestParam(requestParam);
            bioRequestLog.setRequestMethod(requestMethod);
            bioRequestLog.setRequestId(requestId);
            bioRequestLog.setRequestDesc(requestDesc);
            bioRequestLogMapper.insert(bioRequestLog);
        } catch (Exception e) {
            log.error("记录请求日志出错", e);
        }

    }
}
