package com.bio.drqi.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bio.drqi.document.domain.DocPermissionInfo;
import com.bio.drqi.document.dto.DocumentPermissionItemDTO;
import com.bio.drqi.document.dto.DocumentPermissionSaveDTO;
import com.bio.drqi.document.mapper.DocPermissionInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class DocumentPermissionService {

    @Resource
    private DocPermissionInfoMapper docPermissionInfoMapper;

    @Resource
    private DocumentIdentityService documentIdentityService;

    @Resource
    private com.bio.drqi.document.mapper.DocFileInfoMapper docFileInfoMapper;

    @Resource
    private DocumentAiIndexEventService documentAiIndexEventService;

    public List<DocPermissionInfo> list(String resourceType, Long resourceId) {
        if (resourceType == null || resourceId == null) {
            return Collections.emptyList();
        }
        return docPermissionInfoMapper.selectList(new LambdaQueryWrapper<DocPermissionInfo>()
                .eq(DocPermissionInfo::getResourceType, resourceType)
                .eq(DocPermissionInfo::getResourceId, resourceId)
                .orderByDesc(DocPermissionInfo::getId));
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(DocumentPermissionSaveDTO dto) {
        docPermissionInfoMapper.delete(new LambdaQueryWrapper<DocPermissionInfo>()
                .eq(DocPermissionInfo::getResourceType, dto.getResourceType())
                .eq(DocPermissionInfo::getResourceId, dto.getResourceId()));
        if (dto.getPermissions() == null || dto.getPermissions().isEmpty()) {
            return;
        }
        Long userId = documentIdentityService.currentUserId();
        Date now = new Date();
        for (DocumentPermissionItemDTO item : dto.getPermissions()) {
            DocPermissionInfo entity = new DocPermissionInfo();
            BeanUtils.copyProperties(item, entity);
            entity.setResourceType(dto.getResourceType());
            entity.setResourceId(dto.getResourceId());
            entity.setCreateBy(userId);
            entity.setCreateTime(now);
            docPermissionInfoMapper.insert(entity);
        }
        if ("DOCUMENT".equalsIgnoreCase(dto.getResourceType())) {
            documentAiIndexEventService.record(DocumentAiIndexEventService.EVENT_PERMISSION_CHANGED,
                    docFileInfoMapper.selectById(dto.getResourceId()), null);
        }
    }
}
