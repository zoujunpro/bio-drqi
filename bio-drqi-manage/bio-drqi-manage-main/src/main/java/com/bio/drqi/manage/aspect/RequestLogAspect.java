package com.bio.drqi.manage.aspect;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.constant.SecurityConstants;
import com.bio.common.core.util.SpringUtils;
import com.bio.drqi.manage.service.BioRequestService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
@Order(999)
@Aspect
@Component
public class RequestLogAspect {

    /**
     * 环绕操作
     *
     * @param point 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("@annotation(com.bio.drqi.aspect.RequestLog)")
    public Object aroundLog(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object target = point.getTarget();
        RequestLog requestLog = method.getAnnotation(RequestLog.class);
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        String requestId = request.getHeader(SecurityConstants.TRACE_ID_HEADER);
        BioRequestService bioRequestService = SpringUtils.getBean(BioRequestService.class);
        Object[] obj = point.getArgs();
        String requestParam = null;
        if (obj == null || obj.length == 0) {
            requestParam = null;
        } else {
            requestParam = JSONUtil.toJsonStr(obj[0]);
        }
        bioRequestService.logRequest(requestParam, target.getClass().getSimpleName() + "." + method.getName(), requestId, requestLog.value());
        return point.proceed();
    }

}
