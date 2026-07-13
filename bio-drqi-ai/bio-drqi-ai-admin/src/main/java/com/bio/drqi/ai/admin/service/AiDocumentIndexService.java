package com.bio.drqi.ai.admin.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.ai.api.embedding.AiEmbeddingService;
import com.bio.drqi.ai.api.embedding.dto.AiEmbeddingReqDTO;
import com.bio.drqi.ai.api.embedding.dto.AiEmbeddingRspDTO;
import com.bio.drqi.ai.common.constant.AiDocumentIndexConstant;
import com.bio.drqi.ai.common.enums.AiDocumentIndexEventStatusEnum;
import com.bio.drqi.ai.common.enums.AiDocumentIndexEventTypeEnum;
import com.bio.drqi.ai.common.enums.AiDocumentIndexStatusEnum;
import com.bio.drqi.ai.common.enums.AiDocumentPermissionPrincipalTypeEnum;
import com.bio.drqi.ai.common.model.AiDocumentChunk;
import com.bio.drqi.ai.common.model.AiFileParseResult;
import com.bio.drqi.ai.common.spi.AiTokenEstimator;
import com.bio.drqi.ai.common.util.AiDocumentChunkUtil;
import com.bio.drqi.ai.common.util.AiFileParseUtil;
import com.bio.drqi.ai.dao.domain.AiDocumentIndex;
import com.bio.drqi.ai.dao.domain.AiDocumentIndexEvent;
import com.bio.drqi.ai.dao.domain.AiDocumentPermission;
import com.bio.drqi.ai.dao.mapper.AiDocumentIndexEventMapper;
import com.bio.drqi.ai.dao.mapper.AiDocumentIndexMapper;
import com.bio.drqi.ai.dao.mapper.AiDocumentPermissionMapper;
import com.bio.drqi.ai.dto.rag.AiDocumentIndexDeleteReqDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentIndexRspDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentIndexUpsertReqDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentPermissionDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentPermissionRefreshReqDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentSearchItemDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentSearchReqDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentSearchRspDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 文档 RAG 索引服务。
 * 设计边界：
 * 1. 文档系统负责文件上传、解析、权限计算，然后通过接口把正文和权限推送到 AI 服务。
 * 2. AI 服务只维护 RAG 所需的索引元数据、权限快照、索引事件和向量分块。
 * 3. MySQL 保存业务元数据和权限，PGVector 保存可检索文本分块和向量。
 */
@Service
public class AiDocumentIndexService {

    @Resource
    private AiDocumentIndexMapper aiDocumentIndexMapper;

    @Resource
    private AiDocumentPermissionMapper aiDocumentPermissionMapper;

    @Resource
    private AiDocumentIndexEventMapper aiDocumentIndexEventMapper;

    @Resource
    private AiEmbeddingService aiEmbeddingService;

    @Resource
    private OssService ossService;

    /**
     * token 估算器扩展点。
     * 如果后续接入阿里千问 tokenizer，可以注册 AiTokenEstimator Bean；
     * 未注册时 AiDocumentChunkUtil 会自动回退到默认估算器。
     */
    @Autowired(required = false)
    private AiTokenEstimator aiTokenEstimator;

    @Resource
    @Qualifier("pgVectorJdbcTemplate")
    private JdbcTemplate pgVectorJdbcTemplate;

    @Transactional(rollbackFor = Exception.class)
    public AiDocumentIndexRspDTO upsert(AiDocumentIndexUpsertReqDTO reqDTO) {
        // 先记录事件，后续成功或失败都会回写状态，方便排查文档系统推送问题。
        AiDocumentIndexEvent event = saveEvent(reqDTO.getEventId(), AiDocumentIndexEventTypeEnum.UPSERT,
                reqDTO.getDocumentId(), reqDTO.getSourceSystem(), AiDocumentIndexEventStatusEnum.PROCESSING, null);
        try {
            AiFileParseResult parseResult = resolveParseResult(reqDTO);
            String contentText = parseResult.getParsedText();
            String contentHash = sha256(contentText);
            AiDocumentIndex old = findIndex(reqDTO.getDocumentId());
            if (old != null && AiDocumentIndexStatusEnum.READY.getCode().equals(old.getStatus())
                    && contentHash.equals(old.getContentHash())) {
                // 正文没有变化时只刷新权限，避免重复调用 embedding 模型和重复写入向量库。
                refreshPermissions(reqDTO.getDocumentId(), reqDTO.getPermissions());
                updateEvent(event, AiDocumentIndexEventStatusEnum.SUCCESS, null);
                return buildRsp(reqDTO.getDocumentId(), AiDocumentIndexStatusEnum.READY, old.getChunkCount(), contentHash);
            }

            List<AiDocumentChunk> chunks = parseResult.getParseBlocks() == null || parseResult.getParseBlocks().isEmpty()
                    ? AiDocumentChunkUtil.split(contentText, aiTokenEstimator)
                    : AiDocumentChunkUtil.split(parseResult.getParseBlocks(), aiTokenEstimator);
            if (chunks.isEmpty()) {
                throw new BusinessException("文档正文分块为空");
            }

            markPgChunksDeleted(reqDTO.getDocumentId());
            String embeddingModel = null;
            for (AiDocumentChunk chunk : chunks) {
                // 第一版同步生成向量；后续文档量变大时可以改成事件表 + 异步消费。
                AiEmbeddingRspDTO embedding = embed(chunk.getChunkText());
                embeddingModel = embedding.getModel();
                insertPgChunk(reqDTO, chunk, embedding);
            }

            upsertIndex(reqDTO, contentHash, embeddingModel, chunks.size(), AiDocumentIndexStatusEnum.READY);
            refreshPermissions(reqDTO.getDocumentId(), reqDTO.getPermissions());
            updateEvent(event, AiDocumentIndexEventStatusEnum.SUCCESS, null);
            return buildRsp(reqDTO.getDocumentId(), AiDocumentIndexStatusEnum.READY, chunks.size(), contentHash);
        } catch (RuntimeException ex) {
            updateEvent(event, AiDocumentIndexEventStatusEnum.FAILED, ex.getMessage());
            throw ex;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public AiDocumentIndexRspDTO delete(AiDocumentIndexDeleteReqDTO reqDTO) {
        // 删除走软删除，保留索引事件和历史记录，避免误删后无法追溯。
        AiDocumentIndexEvent event = saveEvent(reqDTO.getEventId(), AiDocumentIndexEventTypeEnum.DELETE,
                reqDTO.getDocumentId(), reqDTO.getSourceSystem(), AiDocumentIndexEventStatusEnum.PROCESSING, null);
        try {
            AiDocumentIndex index = findIndex(reqDTO.getDocumentId());
            if (index != null) {
                index.setStatus(AiDocumentIndexStatusEnum.DELETED.getCode());
                index.setUpdateTime(new Date());
                aiDocumentIndexMapper.updateById(index);
            }
            markPgChunksDeleted(reqDTO.getDocumentId());
            deletePermissions(reqDTO.getDocumentId());
            updateEvent(event, AiDocumentIndexEventStatusEnum.SUCCESS, null);
            return buildRsp(reqDTO.getDocumentId(), AiDocumentIndexStatusEnum.DELETED, 0,
                    index == null ? null : index.getContentHash());
        } catch (RuntimeException ex) {
            updateEvent(event, AiDocumentIndexEventStatusEnum.FAILED, ex.getMessage());
            throw ex;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public AiDocumentIndexRspDTO refreshPermission(AiDocumentPermissionRefreshReqDTO reqDTO) {
        // 权限刷新只替换 MySQL 权限快照，不触碰 PGVector 分块，成本低且不会影响已有向量。
        AiDocumentIndexEvent event = saveEvent(reqDTO.getEventId(), AiDocumentIndexEventTypeEnum.PERMISSION_REFRESH,
                reqDTO.getDocumentId(), reqDTO.getSourceSystem(), AiDocumentIndexEventStatusEnum.PROCESSING, null);
        try {
            refreshPermissions(reqDTO.getDocumentId(), reqDTO.getPermissions());
            AiDocumentIndex index = findIndex(reqDTO.getDocumentId());
            updateEvent(event, AiDocumentIndexEventStatusEnum.SUCCESS, null);
            return buildRsp(reqDTO.getDocumentId(),
                    index == null ? AiDocumentIndexStatusEnum.UNKNOWN.getCode() : index.getStatus(),
                    index == null ? 0 : index.getChunkCount(),
                    index == null ? null : index.getContentHash());
        } catch (RuntimeException ex) {
            updateEvent(event, AiDocumentIndexEventStatusEnum.FAILED, ex.getMessage());
            throw ex;
        }
    }

    public AiDocumentSearchRspDTO search(AiDocumentSearchReqDTO reqDTO) {
        // 先在 MySQL 做权限过滤，再把允许访问的 document_id 带到 PGVector 做向量召回。
        List<String> documentIds = allowedDocumentIds(reqDTO);
        AiDocumentSearchRspDTO rspDTO = new AiDocumentSearchRspDTO();
        if (documentIds.isEmpty()) {
            return rspDTO;
        }

        AiEmbeddingRspDTO embedding = embed(reqDTO.getQuery());
        List<Object> args = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder();
        sql.append("select document_id, chunk_id, chunk_no, title, chunk_text, metadata::text as metadata, ");
        sql.append("(1 - (embedding <=> ?::vector)) as score ");
        sql.append("from ai_document_chunk_vector where status = ? and document_id in (");
        args.add(toVectorLiteral(embedding.getEmbedding()));
        args.add(AiDocumentIndexStatusEnum.READY.getCode());
        appendPlaceholders(sql, args, documentIds);
        sql.append(") order by embedding <=> ?::vector limit ?");
        args.add(toVectorLiteral(embedding.getEmbedding()));
        args.add(reqDTO.getTopK() == null || reqDTO.getTopK() <= 0
                ? AiDocumentIndexConstant.DEFAULT_TOP_K : reqDTO.getTopK());

        rspDTO.setItems(pgVectorJdbcTemplate.query(sql.toString(), args.toArray(), new RowMapper<AiDocumentSearchItemDTO>() {
            @Override
            public AiDocumentSearchItemDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                AiDocumentSearchItemDTO item = new AiDocumentSearchItemDTO();
                item.setDocumentId(rs.getString("document_id"));
                item.setChunkId(rs.getString("chunk_id"));
                item.setChunkNo(rs.getInt("chunk_no"));
                item.setTitle(rs.getString("title"));
                item.setContent(rs.getString("chunk_text"));
                item.setMetadata(rs.getString("metadata"));
                item.setScore(rs.getDouble("score"));
                return item;
            }
        }));
        return rspDTO;
    }

    private AiEmbeddingRspDTO embed(String text) {
        AiEmbeddingReqDTO reqDTO = new AiEmbeddingReqDTO();
        reqDTO.setInput(text);
        return aiEmbeddingService.embed(reqDTO);
    }

    private AiFileParseResult resolveParseResult(AiDocumentIndexUpsertReqDTO reqDTO) {
        if (hasText(reqDTO.getContentText())) {
            AiFileParseResult result = new AiFileParseResult();
            result.setParsedText(reqDTO.getContentText());
            result.setSuccess(Boolean.TRUE);
            return result;
        }
        return parseOssDocument(reqDTO);
    }

    private AiFileParseResult parseOssDocument(AiDocumentIndexUpsertReqDTO reqDTO) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("ai-rag-doc-", tempFileSuffix(reqDTO));
            ossService.downloadPath(tempFile.getAbsolutePath(), reqDTO.getFileObject());
            AiFileParseResult parseResult = AiFileParseUtil.parse(tempFile.getAbsolutePath(), reqDTO.getFileType());
            if (!Boolean.TRUE.equals(parseResult.getSuccess())) {
                throw new BusinessException("文档解析失败：" + parseResult.getErrorMessage());
            }
            if (!hasText(parseResult.getParsedText())) {
                throw new BusinessException("文档解析后正文为空");
            }
            return parseResult;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("文档下载或解析失败：" + ex.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private String tempFileSuffix(AiDocumentIndexUpsertReqDTO reqDTO) {
        String sourceName = hasText(reqDTO.getFileName()) ? reqDTO.getFileName() : reqDTO.getFileObject();
        if (hasText(sourceName)) {
            int dotIndex = sourceName.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < sourceName.length() - 1) {
                return sourceName.substring(dotIndex);
            }
        }
        if (hasText(reqDTO.getFileType())) {
            return "." + reqDTO.getFileType().replace(".", "");
        }
        return ".tmp";
    }

    private AiDocumentIndex findIndex(String documentId) {
        QueryWrapper<AiDocumentIndex> wrapper = new QueryWrapper<AiDocumentIndex>();
        wrapper.eq("document_id", documentId).last("limit 1");
        return aiDocumentIndexMapper.selectOne(wrapper);
    }

    private void upsertIndex(AiDocumentIndexUpsertReqDTO reqDTO, String contentHash, String embeddingModel,
                             int chunkCount, AiDocumentIndexStatusEnum status) {
        AiDocumentIndex index = findIndex(reqDTO.getDocumentId());
        Date now = new Date();
        if (index == null) {
            index = new AiDocumentIndex();
            index.setCreateTime(now);
        }
        index.setDocumentId(reqDTO.getDocumentId());
        index.setSourceSystem(reqDTO.getSourceSystem());
        index.setBizType(reqDTO.getBizType());
        index.setBizId(reqDTO.getBizId());
        index.setTitle(reqDTO.getTitle());
        index.setFileName(reqDTO.getFileName());
        index.setFileType(reqDTO.getFileType());
        index.setVersionNo(reqDTO.getVersionNo());
        index.setContentHash(contentHash);
        index.setEmbeddingModel(embeddingModel);
        index.setChunkCount(chunkCount);
        index.setStatus(status.getCode());
        index.setIndexTime(now);
        index.setUpdateTime(now);
        if (index.getId() == null) {
            aiDocumentIndexMapper.insert(index);
        } else {
            aiDocumentIndexMapper.updateById(index);
        }
    }

    private void refreshPermissions(String documentId, List<AiDocumentPermissionDTO> permissions) {
        // 文档系统是权限来源，AI 侧只保存一份检索用快照，所以这里按 document_id 全量替换。
        deletePermissions(documentId);
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        Date now = new Date();
        for (AiDocumentPermissionDTO permissionDTO : permissions) {
            if (permissionDTO == null || !hasText(permissionDTO.getPrincipalType())) {
                continue;
            }
            AiDocumentPermission permission = new AiDocumentPermission();
            permission.setDocumentId(documentId);
            permission.setPrincipalType(permissionDTO.getPrincipalType());
            permission.setPrincipalId(permissionDTO.getPrincipalId());
            permission.setCanRead(1);
            permission.setCreateTime(now);
            permission.setUpdateTime(now);
            aiDocumentPermissionMapper.insert(permission);
        }
    }

    private void deletePermissions(String documentId) {
        QueryWrapper<AiDocumentPermission> wrapper = new QueryWrapper<AiDocumentPermission>();
        wrapper.eq("document_id", documentId);
        aiDocumentPermissionMapper.delete(wrapper);
    }

    private List<String> allowedDocumentIds(AiDocumentSearchReqDTO reqDTO) {
        QueryWrapper<AiDocumentPermission> permissionWrapper = new QueryWrapper<AiDocumentPermission>();
        permissionWrapper.eq("can_read", 1);
        permissionWrapper.and(wrapper -> {
            boolean appended = false;
            if (hasText(reqDTO.getUserId())) {
                wrapper.eq("principal_type", AiDocumentPermissionPrincipalTypeEnum.USER.getCode())
                        .eq("principal_id", reqDTO.getUserId());
                appended = true;
            }
            if (reqDTO.getDeptIds() != null && !reqDTO.getDeptIds().isEmpty()) {
                if (appended) {
                    wrapper.or();
                }
                wrapper.eq("principal_type", AiDocumentPermissionPrincipalTypeEnum.DEPT.getCode())
                        .in("principal_id", reqDTO.getDeptIds());
                appended = true;
            }
            if (reqDTO.getRoleIds() != null && !reqDTO.getRoleIds().isEmpty()) {
                if (appended) {
                    wrapper.or();
                }
                wrapper.eq("principal_type", AiDocumentPermissionPrincipalTypeEnum.ROLE.getCode())
                        .in("principal_id", reqDTO.getRoleIds());
                appended = true;
            }
            if (appended) {
                wrapper.or();
            }
            wrapper.eq("principal_type", AiDocumentPermissionPrincipalTypeEnum.PUBLIC.getCode());
        });
        List<AiDocumentPermission> permissions = aiDocumentPermissionMapper.selectList(permissionWrapper);
        Set<String> ids = new LinkedHashSet<String>();
        for (AiDocumentPermission permission : permissions) {
            ids.add(permission.getDocumentId());
        }
        if (ids.isEmpty()) {
            return new ArrayList<String>();
        }

        QueryWrapper<AiDocumentIndex> indexWrapper = new QueryWrapper<AiDocumentIndex>();
        // 权限表只说明“能不能看”，索引表再确认文档是否仍然 READY，以及是否命中业务范围。
        indexWrapper.select("document_id").eq("status", AiDocumentIndexStatusEnum.READY.getCode()).in("document_id", ids);
        if (hasText(reqDTO.getBizType())) {
            indexWrapper.eq("biz_type", reqDTO.getBizType());
        }
        if (hasText(reqDTO.getBizId())) {
            indexWrapper.eq("biz_id", reqDTO.getBizId());
        }
        List<AiDocumentIndex> indexes = aiDocumentIndexMapper.selectList(indexWrapper);
        List<String> result = new ArrayList<String>();
        for (AiDocumentIndex index : indexes) {
            result.add(index.getDocumentId());
        }
        return result;
    }

    private void insertPgChunk(AiDocumentIndexUpsertReqDTO reqDTO, AiDocumentChunk chunk,
                               AiEmbeddingRspDTO embedding) {
        String chunkId = reqDTO.getDocumentId() + "-" + chunk.getChunkNo();
        JSONObject metadata = new JSONObject();
        if (reqDTO.getMetadata() != null) {
            for (Map.Entry<String, Object> entry : reqDTO.getMetadata().entrySet()) {
                metadata.put(entry.getKey(), entry.getValue());
            }
        }
        metadata.put("sourceSystem", reqDTO.getSourceSystem());
        metadata.put("bizType", reqDTO.getBizType());
        metadata.put("bizId", reqDTO.getBizId());
        metadata.put("fileName", reqDTO.getFileName());
        metadata.put("fileType", reqDTO.getFileType());
        metadata.put("versionNo", reqDTO.getVersionNo());
        metadata.put("sectionTitle", chunk.getSectionTitle());
        metadata.put("pageNo", chunk.getPageNo());
        metadata.put("sheetName", chunk.getSheetName());
        metadata.put("chunkType", chunk.getChunkType());
        metadata.put("tokenCount", chunk.getTokenCount());

        pgVectorJdbcTemplate.update(
                // pgvector 的 vector 类型和 <=> 运算更适合原生 SQL，这里不走 MyBatis-Plus。
                "insert into ai_document_chunk_vector " +
                        "(chunk_id, document_id, chunk_no, title, chunk_text, section_title, page_no, token_count, content_hash, metadata, embedding_model, embedding, status, create_time, update_time) " +
                        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?::vector, ?, now(), now())",
                chunkId,
                reqDTO.getDocumentId(),
                chunk.getChunkNo(),
                reqDTO.getTitle(),
                chunk.getChunkText(),
                chunk.getSectionTitle(),
                chunk.getPageNo(),
                chunk.getTokenCount(),
                sha256(chunk.getChunkText()),
                metadata.toJSONString(),
                embedding.getModel(),
                toVectorLiteral(embedding.getEmbedding()),
                AiDocumentIndexStatusEnum.READY.getCode()
        );
    }

    private void markPgChunksDeleted(String documentId) {
        pgVectorJdbcTemplate.update("update ai_document_chunk_vector set status = ?, update_time = now() where document_id = ?",
                AiDocumentIndexStatusEnum.DELETED.getCode(), documentId);
    }

    private AiDocumentIndexEvent saveEvent(String eventId, AiDocumentIndexEventTypeEnum eventType, String documentId,
                                           String sourceSystem, AiDocumentIndexEventStatusEnum status, String errorMessage) {
        AiDocumentIndexEvent event = new AiDocumentIndexEvent();
        event.setEventId(eventId);
        event.setEventType(eventType.getCode());
        event.setDocumentId(documentId);
        event.setSourceSystem(sourceSystem);
        event.setStatus(status.getCode());
        event.setErrorMessage(limit(errorMessage, AiDocumentIndexConstant.EVENT_ERROR_MESSAGE_MAX_LENGTH));
        event.setCreateTime(new Date());
        event.setUpdateTime(new Date());
        aiDocumentIndexEventMapper.insert(event);
        return event;
    }

    private void updateEvent(AiDocumentIndexEvent event, AiDocumentIndexEventStatusEnum status, String errorMessage) {
        if (event == null || event.getId() == null) {
            return;
        }
        event.setStatus(status.getCode());
        event.setErrorMessage(limit(errorMessage, AiDocumentIndexConstant.EVENT_ERROR_MESSAGE_MAX_LENGTH));
        event.setUpdateTime(new Date());
        aiDocumentIndexEventMapper.updateById(event);
    }

    private AiDocumentIndexRspDTO buildRsp(String documentId, AiDocumentIndexStatusEnum status, Integer chunkCount,
                                           String contentHash) {
        return buildRsp(documentId, status.getCode(), chunkCount, contentHash);
    }

    private AiDocumentIndexRspDTO buildRsp(String documentId, String status, Integer chunkCount, String contentHash) {
        AiDocumentIndexRspDTO rspDTO = new AiDocumentIndexRspDTO();
        rspDTO.setDocumentId(documentId);
        rspDTO.setStatus(status);
        rspDTO.setChunkCount(chunkCount);
        rspDTO.setContentHash(contentHash);
        return rspDTO;
    }

    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception ex) {
            throw new BusinessException("计算内容 hash 失败：" + ex.getMessage());
        }
    }

    private String toVectorLiteral(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            throw new BusinessException("向量结果为空");
        }
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < embedding.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(embedding.get(i));
        }
        builder.append(']');
        return builder.toString();
    }

    private void appendPlaceholders(StringBuilder sql, List<Object> args, List<String> values) {
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
            args.add(values.get(i));
        }
    }

    private String limit(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
