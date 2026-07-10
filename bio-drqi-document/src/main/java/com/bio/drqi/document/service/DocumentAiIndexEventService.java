package com.bio.drqi.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.drqi.document.domain.DocAiIndexEvent;
import com.bio.drqi.document.domain.DocFileInfo;
import com.bio.drqi.document.domain.DocVersionHis;
import com.bio.drqi.document.mapper.DocAiIndexEventMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class DocumentAiIndexEventService {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_IGNORED = "IGNORED";

    public static final String EVENT_DOCUMENT_UPLOADED = "DOCUMENT_UPLOADED";
    public static final String EVENT_VERSION_UPLOADED = "VERSION_UPLOADED";
    public static final String EVENT_DOCUMENT_DELETED = "DOCUMENT_DELETED";
    public static final String EVENT_PERMISSION_CHANGED = "PERMISSION_CHANGED";

    @Resource
    private DocAiIndexEventMapper docAiIndexEventMapper;

    public void record(String eventType, DocFileInfo document, DocVersionHis version) {
        if (document == null) {
            return;
        }
        DocAiIndexEvent event = new DocAiIndexEvent();
        event.setEventType(eventType);
        event.setDocumentId(document.getId());
        event.setVersionId(version == null ? document.getCurrentVersionId() : version.getId());
        event.setDocCode(document.getDocCode());
        event.setDocName(document.getDocName());
        event.setFilePath(version == null ? null : version.getFilePath());
        event.setFileType(version == null ? document.getFileType() : version.getFileType());
        event.setStatus(STATUS_PENDING);
        event.setRetryCount(0);
        event.setCreateTime(new Date());
        event.setUpdateTime(new Date());
        docAiIndexEventMapper.insert(event);
    }

    public Page<DocAiIndexEvent> page(Integer pageNum, Integer pageSize, String status, String eventType) {
        return docAiIndexEventMapper.selectPage(new Page<DocAiIndexEvent>(pageNum == null ? 1 : pageNum, pageSize == null ? 20 : pageSize),
                new LambdaQueryWrapper<DocAiIndexEvent>()
                        .eq(StringUtils.isNotBlank(status), DocAiIndexEvent::getStatus, status)
                        .eq(StringUtils.isNotBlank(eventType), DocAiIndexEvent::getEventType, eventType)
                        .orderByDesc(DocAiIndexEvent::getId));
    }
}
