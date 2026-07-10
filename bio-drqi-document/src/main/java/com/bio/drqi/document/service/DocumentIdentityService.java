package com.bio.drqi.document.service;

import com.bio.common.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DocumentIdentityService {

    public Long currentUserId() {
        Integer userId = SecurityContextHolder.getUserId();
        return userId == null ? null : userId.longValue();
    }

    public String currentUserName() {
        return SecurityContextHolder.getNickName();
    }
}
