package com.bio.drqi.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.document.domain.DocFileInfo;
import com.bio.drqi.document.domain.DocFavoriteRel;
import com.bio.drqi.document.domain.DocOperationLog;
import com.bio.drqi.document.domain.DocRecycleInfo;
import com.bio.drqi.document.domain.DocShareInfo;
import com.bio.drqi.document.domain.DocVersionHis;
import com.bio.drqi.document.domain.DocViewLog;
import com.bio.drqi.document.dto.DocumentDownloadRspDTO;
import com.bio.drqi.document.dto.DocumentPageReqDTO;
import com.bio.drqi.document.dto.DocumentUploadRspDTO;
import com.bio.drqi.document.mapper.DocFavoriteRelMapper;
import com.bio.drqi.document.mapper.DocFileInfoMapper;
import com.bio.drqi.document.mapper.DocOperationLogMapper;
import com.bio.drqi.document.mapper.DocRecycleInfoMapper;
import com.bio.drqi.document.mapper.DocShareInfoMapper;
import com.bio.drqi.document.mapper.DocVersionHisMapper;
import com.bio.drqi.document.mapper.DocViewLogMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentFileService {

    @Resource
    private DocFileInfoMapper docFileInfoMapper;

    @Resource
    private DocVersionHisMapper docVersionHisMapper;

    @Resource
    private DocFavoriteRelMapper docFavoriteRelMapper;

    @Resource
    private DocRecycleInfoMapper docRecycleInfoMapper;

    @Resource
    private DocShareInfoMapper docShareInfoMapper;

    @Resource
    private DocOperationLogMapper docOperationLogMapper;

    @Resource
    private DocViewLogMapper docViewLogMapper;

    @Resource
    private OssService ossService;

    @Resource
    private DocumentIdentityService documentIdentityService;

    @Resource
    private DocumentCodeService documentCodeService;

    @Resource
    private DocumentAiIndexEventService documentAiIndexEventService;

    public Page<DocFileInfo> page(DocumentPageReqDTO req) {
        LambdaQueryWrapper<DocFileInfo> wrapper = new LambdaQueryWrapper<DocFileInfo>()
                .eq(DocFileInfo::getDeleted, "N")
                .eq(req.getCategoryId() != null, DocFileInfo::getCategoryId, req.getCategoryId())
                .eq(StringUtils.isNotBlank(req.getSpaceType()), DocFileInfo::getSpaceType, req.getSpaceType())
                .eq(StringUtils.isNotBlank(req.getStatus()), DocFileInfo::getStatus, req.getStatus())
                .and(StringUtils.isNotBlank(req.getKeyword()), q -> q
                        .like(DocFileInfo::getDocName, req.getKeyword())
                        .or()
                        .like(DocFileInfo::getDocCode, req.getKeyword()))
                .orderByDesc(DocFileInfo::getUpdateTime)
                .orderByDesc(DocFileInfo::getId);
        return pageByLimit(req, wrapper);
    }

    public Page<DocFileInfo> myPage(DocumentPageReqDTO req) {
        LambdaQueryWrapper<DocFileInfo> wrapper = baseListWrapper(req)
                .eq(DocFileInfo::getOwnerUserId, documentIdentityService.currentUserId());
        return pageByLimit(req, wrapper);
    }

    public Page<DocFileInfo> favoritePage(DocumentPageReqDTO req) {
        Long userId = documentIdentityService.currentUserId();
        LambdaQueryWrapper<DocFileInfo> wrapper = baseListWrapper(req)
                .inSql(DocFileInfo::getId, "select file_id from doc_favorite_rel where user_id = " + userId);
        return pageByLimit(req, wrapper);
    }

    public Page<DocFileInfo> sharePage(DocumentPageReqDTO req) {
        Long userId = documentIdentityService.currentUserId();
        LambdaQueryWrapper<DocFileInfo> wrapper = baseListWrapper(req)
                .inSql(DocFileInfo::getId, "select file_id from doc_share_info where share_user_id = " + userId + " and status = 'ACTIVE'");
        return pageByLimit(req, wrapper);
    }

    public Page<DocFileInfo> recentPage(DocumentPageReqDTO req) {
        Long userId = documentIdentityService.currentUserId();
        LambdaQueryWrapper<DocFileInfo> wrapper = baseListWrapper(req)
                .inSql(DocFileInfo::getId, "select file_id from doc_view_log where user_id = " + userId);
        return pageByLimit(req, wrapper);
    }

    public Page<DocFileInfo> recyclePage(DocumentPageReqDTO req) {
        Long userId = documentIdentityService.currentUserId();
        LambdaQueryWrapper<DocFileInfo> wrapper = new LambdaQueryWrapper<DocFileInfo>()
                .eq(DocFileInfo::getDeleted, "Y")
                .inSql(DocFileInfo::getId, "select file_id from doc_recycle_info where delete_user_id = " + userId + " and status = 'RECYCLED'")
                .and(StringUtils.isNotBlank(req.getKeyword()), q -> q
                        .like(DocFileInfo::getDocName, req.getKeyword())
                        .or()
                        .like(DocFileInfo::getDocCode, req.getKeyword()))
                .orderByDesc(DocFileInfo::getUpdateTime)
                .orderByDesc(DocFileInfo::getId);
        return pageByLimit(req, wrapper);
    }

    private Page<DocFileInfo> pageByLimit(DocumentPageReqDTO req, LambdaQueryWrapper<DocFileInfo> wrapper) {
        int pageNum = req.getPageNum() == null || req.getPageNum() < 1 ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null || req.getPageSize() < 1 ? 20 : Math.min(req.getPageSize(), 100);
        Long total = docFileInfoMapper.selectCount(wrapper);
        List<DocFileInfo> records = docFileInfoMapper.selectList(wrapper.last("LIMIT " + ((pageNum - 1) * pageSize) + "," + pageSize));
        fillFavoriteStatus(records);
        Page<DocFileInfo> page = new Page<>(pageNum, pageSize);
        page.setTotal(total == null ? 0 : total);
        page.setRecords(records);
        return page;
    }

    private void fillFavoriteStatus(List<DocFileInfo> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Long userId = documentIdentityService.currentUserId();
        Set<Long> fileIds = records.stream()
                .map(DocFileInfo::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (fileIds.isEmpty()) {
            return;
        }
        List<DocFavoriteRel> favorites = docFavoriteRelMapper.selectList(new LambdaQueryWrapper<DocFavoriteRel>()
                .eq(DocFavoriteRel::getUserId, userId)
                .in(DocFavoriteRel::getFileId, fileIds));
        Set<Long> favoriteFileIds = favorites == null ? Collections.emptySet() : favorites.stream()
                .map(DocFavoriteRel::getFileId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        records.forEach(record -> record.setIsFavorite(favoriteFileIds.contains(record.getId())));
    }

    public Page<DocOperationLog> operationLogPage(Long documentId, DocumentPageReqDTO req) {
        LambdaQueryWrapper<DocOperationLog> wrapper = new LambdaQueryWrapper<DocOperationLog>()
                .eq(documentId != null, DocOperationLog::getFileId, documentId)
                .orderByDesc(DocOperationLog::getCreateTime)
                .orderByDesc(DocOperationLog::getId);
        return docOperationLogMapper.selectPage(new Page<>(req.getPageNum(), req.getPageSize()), wrapper);
    }

    public DocFileInfo detail(Long id) {
        DocFileInfo document = requireDocument(id, false);
        fillFavoriteStatus(Collections.singletonList(document));
        recordView(document.getId(), document.getCurrentVersionId());
        return document;
    }

    public List<DocVersionHis> versions(Long documentId) {
        return docVersionHisMapper.selectList(new LambdaQueryWrapper<DocVersionHis>()
                .eq(DocVersionHis::getFileId, documentId)
                .orderByDesc(DocVersionHis::getCreateTime)
                .orderByDesc(DocVersionHis::getId));
    }

    @Transactional(rollbackFor = Exception.class)
    public DocumentUploadRspDTO upload(Long categoryId, String spaceType, String remark, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            originalFilename = "未命名文件";
        }
        String fileType = resolveFileType(originalFilename);
        Long userId = documentIdentityService.currentUserId();
        Date now = new Date();
        String docCode = documentCodeService.nextCode("DOC");
        String storedFileName = docCode + "-" + UUID.randomUUID().toString().replace("-", "") + extension(originalFilename);
        String dir = "document/" + new SimpleDateFormat("yyyyMMdd").format(now);
        ossService.upload(file, dir, storedFileName);
        String objectName = dir + "/" + storedFileName;

        DocFileInfo document = new DocFileInfo();
        document.setCategoryId(categoryId);
        document.setDocName(originalFilename);
        document.setDocCode(docCode);
        document.setFileType(fileType);
        document.setFileSize(file.getSize());
        document.setOwnerUserId(userId);
        document.setSpaceType(StringUtils.defaultIfBlank(spaceType, "ENTERPRISE"));
        document.setPermissionMode("INHERIT");
        document.setEffectiveType("PERMANENT");
        document.setStatus("PUBLISHED");
        document.setCreateBy(userId);
        document.setCreateTime(now);
        document.setUpdateBy(userId);
        document.setUpdateTime(now);
        document.setDeleted("N");
        document.setRemark(remark);
        docFileInfoMapper.insert(document);

        DocVersionHis version = new DocVersionHis();
        version.setFileId(document.getId());
        version.setVersionNo("v1.0");
        version.setFileName(originalFilename);
        version.setFilePath(objectName);
        version.setFileMd5(md5(file));
        version.setFileSize(file.getSize());
        version.setFileType(fileType);
        version.setChangeLog("初始版本");
        version.setIsCurrent(1);
        version.setCreateBy(userId);
        version.setCreateTime(now);
        docVersionHisMapper.insert(version);

        DocFileInfo update = new DocFileInfo();
        update.setId(document.getId());
        update.setCurrentVersionId(version.getId());
        docFileInfoMapper.updateById(update);

        DocumentUploadRspDTO response = new DocumentUploadRspDTO();
        response.setDocumentId(document.getId());
        response.setVersionId(version.getId());
        response.setDocName(document.getDocName());
        response.setDocCode(document.getDocCode());
        response.setFilePath(objectName);
        response.setDownloadUrl(ossService.getPresignedObjectUrl(objectName));
        documentAiIndexEventService.record(DocumentAiIndexEventService.EVENT_DOCUMENT_UPLOADED, document, version);
        recordLog(document.getId(), version.getId(), "UPLOAD", "上传文档");
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public DocumentUploadRspDTO uploadVersion(Long documentId, String changeLog, MultipartFile file) {
        DocFileInfo document = docFileInfoMapper.selectById(documentId);
        if (document == null || "Y".equals(document.getDeleted())) {
            throw new BusinessException("文档不存在");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        Date now = new Date();
        String originalFilename = StringUtils.defaultIfBlank(file.getOriginalFilename(), document.getDocName());
        String nextVersionNo = nextVersionNo(documentId);
        String storedFileName = document.getDocCode() + "-" + nextVersionNo.replace(".", "_") + "-" + UUID.randomUUID().toString().replace("-", "") + extension(originalFilename);
        String dir = "document/" + new SimpleDateFormat("yyyyMMdd").format(now);
        ossService.upload(file, dir, storedFileName);
        String objectName = dir + "/" + storedFileName;

        List<DocVersionHis> oldVersions = versions(documentId);
        for (DocVersionHis oldVersion : oldVersions) {
            if (Integer.valueOf(1).equals(oldVersion.getIsCurrent())) {
                oldVersion.setIsCurrent(0);
                docVersionHisMapper.updateById(oldVersion);
            }
        }

        DocVersionHis version = new DocVersionHis();
        version.setFileId(documentId);
        version.setVersionNo(nextVersionNo);
        version.setFileName(originalFilename);
        version.setFilePath(objectName);
        version.setFileMd5(md5(file));
        version.setFileSize(file.getSize());
        version.setFileType(resolveFileType(originalFilename));
        version.setChangeLog(StringUtils.defaultIfBlank(changeLog, "上传新版本"));
        version.setIsCurrent(1);
        version.setCreateBy(documentIdentityService.currentUserId());
        version.setCreateTime(now);
        docVersionHisMapper.insert(version);

        DocFileInfo update = new DocFileInfo();
        update.setId(documentId);
        update.setDocName(originalFilename);
        update.setFileType(version.getFileType());
        update.setFileSize(file.getSize());
        update.setCurrentVersionId(version.getId());
        update.setUpdateBy(documentIdentityService.currentUserId());
        update.setUpdateTime(now);
        docFileInfoMapper.updateById(update);

        DocumentUploadRspDTO response = new DocumentUploadRspDTO();
        response.setDocumentId(documentId);
        response.setVersionId(version.getId());
        response.setDocName(originalFilename);
        response.setDocCode(document.getDocCode());
        response.setFilePath(objectName);
        response.setDownloadUrl(ossService.getPresignedObjectUrl(objectName));
        document.setDocName(originalFilename);
        document.setFileType(version.getFileType());
        document.setFileSize(file.getSize());
        document.setCurrentVersionId(version.getId());
        documentAiIndexEventService.record(DocumentAiIndexEventService.EVENT_VERSION_UPLOADED, document, version);
        recordLog(documentId, version.getId(), "VERSION", "上传新版本");
        return response;
    }

    public DocumentDownloadRspDTO downloadUrl(Long documentId, Long versionId) {
        DocFileInfo document = docFileInfoMapper.selectById(documentId);
        if (document == null || "Y".equals(document.getDeleted())) {
            throw new BusinessException("文档不存在");
        }
        DocVersionHis version = versionId == null
                ? docVersionHisMapper.selectById(document.getCurrentVersionId())
                : docVersionHisMapper.selectById(versionId);
        if (version == null || !documentId.equals(version.getFileId())) {
            throw new BusinessException("文档版本不存在");
        }
        DocumentDownloadRspDTO response = new DocumentDownloadRspDTO();
        response.setDocumentId(documentId);
        response.setVersionId(version.getId());
        response.setFileName(version.getFileName());
        response.setFilePath(version.getFilePath());
        response.setDownloadUrl(ossService.getPresignedObjectUrl(version.getFilePath()));
        recordView(documentId, version.getId());
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public DocFileInfo shareDetail(String token) {
        DocShareInfo share = requireActiveShare(token);
        DocFileInfo document = requireDocument(share.getFileId(), false);
        share.setAccessCount(share.getAccessCount() == null ? 1 : share.getAccessCount() + 1);
        share.setUpdateTime(new Date());
        docShareInfoMapper.updateById(share);
        recordView(document.getId(), document.getCurrentVersionId());
        recordLog(document.getId(), null, "SHARE_VIEW", "通过分享链接查看文档");
        return document;
    }

    public DocumentDownloadRspDTO shareDownloadUrl(String token) {
        DocShareInfo share = requireActiveShare(token);
        if (!Integer.valueOf(1).equals(share.getCanDownload())) {
            throw new BusinessException("该分享不允许下载");
        }
        return downloadUrl(share.getFileId(), null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DocFileInfo old = docFileInfoMapper.selectById(id);
        if (old == null || "Y".equals(old.getDeleted())) {
            throw new BusinessException("文档不存在");
        }
        DocFileInfo entity = new DocFileInfo();
        entity.setId(id);
        entity.setDeleted("Y");
        entity.setUpdateBy(documentIdentityService.currentUserId());
        entity.setUpdateTime(new Date());
        docFileInfoMapper.updateById(entity);
        DocRecycleInfo recycle = new DocRecycleInfo();
        recycle.setFileId(id);
        recycle.setDeleteUserId(documentIdentityService.currentUserId());
        recycle.setDeleteTime(new Date());
        recycle.setExpireTime(afterDays(30));
        recycle.setStatus("RECYCLED");
        docRecycleInfoMapper.insert(recycle);
        if (old != null) {
            documentAiIndexEventService.record(DocumentAiIndexEventService.EVENT_DOCUMENT_DELETED, old, null);
        }
        recordLog(id, null, "DELETE", "删除文档到回收站");
    }

    @Transactional(rollbackFor = Exception.class)
    public void restore(Long id) {
        DocFileInfo old = docFileInfoMapper.selectById(id);
        if (old == null) {
            throw new BusinessException("文档不存在");
        }
        Date now = new Date();
        DocFileInfo entity = new DocFileInfo();
        entity.setId(id);
        entity.setDeleted("N");
        entity.setUpdateBy(documentIdentityService.currentUserId());
        entity.setUpdateTime(now);
        docFileInfoMapper.updateById(entity);
        DocRecycleInfo recycle = latestRecycle(id);
        if (recycle != null) {
            recycle.setStatus("RESTORED");
            recycle.setRestoreTime(now);
            docRecycleInfoMapper.updateById(recycle);
        }
        recordLog(id, null, "RESTORE", "从回收站恢复文档");
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteForever(Long id) {
        DocFileInfo old = docFileInfoMapper.selectById(id);
        if (old == null) {
            throw new BusinessException("文档不存在");
        }
        DocFileInfo entity = new DocFileInfo();
        entity.setId(id);
        entity.setStatus("DELETED");
        entity.setUpdateBy(documentIdentityService.currentUserId());
        entity.setUpdateTime(new Date());
        docFileInfoMapper.updateById(entity);
        DocRecycleInfo recycle = latestRecycle(id);
        if (recycle != null) {
            recycle.setStatus("CLEARED");
            docRecycleInfoMapper.updateById(recycle);
        }
        documentAiIndexEventService.record(DocumentAiIndexEventService.EVENT_DOCUMENT_DELETED, old, null);
        recordLog(id, null, "DELETE_FOREVER", "从回收站彻底删除文档");
    }

    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long id) {
        requireDocument(id, false);
        Long userId = documentIdentityService.currentUserId();
        Long count = docFavoriteRelMapper.selectCount(new LambdaQueryWrapper<DocFavoriteRel>()
                .eq(DocFavoriteRel::getUserId, userId)
                .eq(DocFavoriteRel::getFileId, id));
        if (count != null && count > 0) {
            return;
        }
        DocFavoriteRel favorite = new DocFavoriteRel();
        favorite.setUserId(userId);
        favorite.setFileId(id);
        favorite.setCreateTime(new Date());
        docFavoriteRelMapper.insert(favorite);
        recordLog(id, null, "FAVORITE", "收藏文档");
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long id) {
        Long userId = documentIdentityService.currentUserId();
        docFavoriteRelMapper.delete(new LambdaQueryWrapper<DocFavoriteRel>()
                .eq(DocFavoriteRel::getUserId, userId)
                .eq(DocFavoriteRel::getFileId, id));
        recordLog(id, null, "UNFAVORITE", "取消收藏文档");
    }

    @Transactional(rollbackFor = Exception.class)
    public DocShareInfo addShare(Long id, Integer expireDays) {
        requireDocument(id, false);
        Date now = new Date();
        DocShareInfo share = new DocShareInfo();
        share.setFileId(id);
        share.setShareUserId(documentIdentityService.currentUserId());
        share.setShareType("LINK");
        share.setShareToken(UUID.randomUUID().toString().replace("-", ""));
        share.setCanView(1);
        share.setCanDownload(1);
        share.setCanEdit(0);
        share.setExpireTime(afterDays(expireDays == null || expireDays <= 0 ? 7 : expireDays));
        share.setAccessCount(0);
        share.setStatus("ACTIVE");
        share.setCreateTime(now);
        share.setUpdateTime(now);
        docShareInfoMapper.insert(share);
        recordLog(id, null, "SHARE", "创建分享链接");
        return share;
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelShare(Long id) {
        Long userId = documentIdentityService.currentUserId();
        List<DocShareInfo> shares = docShareInfoMapper.selectList(new LambdaQueryWrapper<DocShareInfo>()
                .eq(DocShareInfo::getFileId, id)
                .eq(DocShareInfo::getShareUserId, userId)
                .eq(DocShareInfo::getStatus, "ACTIVE"));
        for (DocShareInfo share : shares) {
            share.setStatus("DISABLED");
            share.setUpdateTime(new Date());
            docShareInfoMapper.updateById(share);
        }
        recordLog(id, null, "CANCEL_SHARE", "取消分享链接");
    }

    private LambdaQueryWrapper<DocFileInfo> baseListWrapper(DocumentPageReqDTO req) {
        return new LambdaQueryWrapper<DocFileInfo>()
                .eq(DocFileInfo::getDeleted, "N")
                .eq(req.getCategoryId() != null, DocFileInfo::getCategoryId, req.getCategoryId())
                .eq(StringUtils.isNotBlank(req.getSpaceType()), DocFileInfo::getSpaceType, req.getSpaceType())
                .eq(StringUtils.isNotBlank(req.getStatus()), DocFileInfo::getStatus, req.getStatus())
                .and(StringUtils.isNotBlank(req.getKeyword()), q -> q
                        .like(DocFileInfo::getDocName, req.getKeyword())
                        .or()
                        .like(DocFileInfo::getDocCode, req.getKeyword()))
                .orderByDesc(DocFileInfo::getUpdateTime)
                .orderByDesc(DocFileInfo::getId);
    }

    private DocFileInfo requireDocument(Long id, boolean includeDeleted) {
        DocFileInfo document = docFileInfoMapper.selectById(id);
        if (document == null || (!includeDeleted && "Y".equals(document.getDeleted()))) {
            throw new BusinessException("文档不存在");
        }
        return document;
    }

    private DocShareInfo requireActiveShare(String token) {
        if (StringUtils.isBlank(token)) {
            throw new BusinessException("分享链接无效");
        }
        DocShareInfo share = docShareInfoMapper.selectOne(new LambdaQueryWrapper<DocShareInfo>()
                .eq(DocShareInfo::getShareToken, token)
                .eq(DocShareInfo::getStatus, "ACTIVE")
                .last("limit 1"));
        if (share == null) {
            throw new BusinessException("分享链接不存在或已失效");
        }
        if (share.getExpireTime() != null && share.getExpireTime().before(new Date())) {
            share.setStatus("EXPIRED");
            share.setUpdateTime(new Date());
            docShareInfoMapper.updateById(share);
            throw new BusinessException("分享链接已过期");
        }
        if (!Integer.valueOf(1).equals(share.getCanView())) {
            throw new BusinessException("该分享不允许查看");
        }
        return share;
    }

    private DocRecycleInfo latestRecycle(Long id) {
        List<DocRecycleInfo> list = docRecycleInfoMapper.selectList(new LambdaQueryWrapper<DocRecycleInfo>()
                .eq(DocRecycleInfo::getFileId, id)
                .orderByDesc(DocRecycleInfo::getId)
                .last("limit 1"));
        return list.isEmpty() ? null : list.get(0);
    }

    private Date afterDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    private void recordLog(Long fileId, Long versionId, String operationType, String content) {
        DocOperationLog log = new DocOperationLog();
        log.setFileId(fileId);
        log.setVersionId(versionId);
        log.setOperationType(operationType);
        log.setOperationContent(content);
        log.setOperatorId(documentIdentityService.currentUserId());
        log.setOperatorName(documentIdentityService.currentUserName());
        log.setCreateTime(new Date());
        docOperationLogMapper.insert(log);
    }

    private void recordView(Long fileId, Long versionId) {
        DocViewLog log = new DocViewLog();
        log.setFileId(fileId);
        log.setVersionId(versionId);
        log.setUserId(documentIdentityService.currentUserId());
        log.setViewTime(new Date());
        docViewLogMapper.insert(log);
    }

    private String nextVersionNo(Long documentId) {
        Long count = docVersionHisMapper.selectCount(new LambdaQueryWrapper<DocVersionHis>()
                .eq(DocVersionHis::getFileId, documentId));
        return "v" + (count == null ? 1 : count + 1) + ".0";
    }

    private String md5(MultipartFile file) {
        try {
            return DigestUtils.md5DigestAsHex(file.getInputStream());
        } catch (IOException e) {
            throw new BusinessException("计算文件MD5失败");
        }
    }

    private String resolveFileType(String fileName) {
        String ext = extension(fileName);
        return ext.startsWith(".") ? ext.substring(1).toLowerCase() : ext.toLowerCase();
    }

    private String extension(String fileName) {
        int index = fileName == null ? -1 : fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(index) : "";
    }
}
