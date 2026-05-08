package com.bio.drqi.es.service.impl;

import com.bio.drqi.es.dto.req.TableSyncReqDTO;
import com.bio.drqi.es.dto.req.TablesSyncReqDTO;
import com.bio.drqi.es.service.EsCommonService;
import com.bio.drqi.es.service.EsSyncService;
import com.bio.drqi.es.support.DomainEntityResolver;
import com.bio.drqi.es.support.EsDocumentConverter;
import com.bio.drqi.es.support.global.GlobalSearchSyncService;
import com.bio.drqi.es.support.EsMappingBuilder;
import com.bio.drqi.es.support.MapperTableQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class EsSyncServiceImpl implements EsSyncService {

    private static final String ID_FIELD = "id";

    private final EsCommonService esCommonService;
    private final DomainEntityResolver domainEntityResolver;
    private final EsMappingBuilder esMappingBuilder;
    private final EsDocumentConverter esDocumentConverter;
    private final MapperTableQueryService mapperTableQueryService;
    private final GlobalSearchSyncService globalSearchSyncService;

    public EsSyncServiceImpl(EsCommonService esCommonService,
                             DomainEntityResolver domainEntityResolver,
                             EsMappingBuilder esMappingBuilder,
                             EsDocumentConverter esDocumentConverter,
                             MapperTableQueryService mapperTableQueryService,
                             GlobalSearchSyncService globalSearchSyncService) {
        this.esCommonService = esCommonService;
        this.domainEntityResolver = domainEntityResolver;
        this.esMappingBuilder = esMappingBuilder;
        this.esDocumentConverter = esDocumentConverter;
        this.mapperTableQueryService = mapperTableQueryService;
        this.globalSearchSyncService = globalSearchSyncService;
    }

    @Override
    public void syncTable(TableSyncReqDTO tableSyncReqDTO) {

        long start = System.currentTimeMillis();
        String table = parseTableName(tableSyncReqDTO.getTableName());
        String index = table.toLowerCase(Locale.ROOT);
        log.info("ES 全量同步开始 table={}, index={}", table, index);
        Class<?> entityClass = domainEntityResolver.resolveEntityClass(table);
        if (entityClass == null) {
            throw new IllegalStateException("在包 com.bio.drqi.domain 下找不到表对应实体: " + table);
        }
        log.info("ES 全量同步解析实体成功 table={}, entityClass={}", table, entityClass.getName());
        Map<String, Object> mapping = esMappingBuilder.buildMappingByEntity(entityClass);
        int fieldCount = ((Map<?, ?>) mapping.get("properties")).size();
        if (fieldCount == 0) {
            throw new IllegalStateException("实体无可用字段: " + entityClass.getName());
        }
        log.info("ES 全量同步 mapping 构建完成 table={}, fieldCount={}", table, fieldCount);
        esCommonService.recreateIndex(index, mapping);
        globalSearchSyncService.deleteByTable(table);

        List<Map<String, Object>> rows = queryRowsByMapper(table);
        log.info("ES 全量同步查询数据库完成 table={}, rows={}", table, rows.size());
        esCommonService.saveBatch(index, ID_FIELD, rows);
        globalSearchSyncService.saveBatch(table, rows);
        log.info("ES 全量同步完成 table={}, index={}, rows={}, costMs={}",
                table, index, rows.size(), System.currentTimeMillis() - start);
    }

    @Override
    public void syncTables(TablesSyncReqDTO tablesSyncReqDTO) {
        if (tablesSyncReqDTO == null || tablesSyncReqDTO.getTableNames() == null || tablesSyncReqDTO.getTableNames().isEmpty()) {
            throw new IllegalStateException("参数缺少：表名列表");
        }
        long start = System.currentTimeMillis();
        int success = 0;
        log.info("ES 批量全量同步开始 tables={}", tablesSyncReqDTO.getTableNames());
        for (String tableName : tablesSyncReqDTO.getTableNames()) {
            if (tableName == null || tableName.trim().isEmpty()) {
                continue;
            }
            TableSyncReqDTO tableSyncReqDTO = new TableSyncReqDTO();
            tableSyncReqDTO.setTableName(tableName);
            syncTable(tableSyncReqDTO);
            success++;
        }
        log.info("ES 批量全量同步完成 tables={}, success={}, costMs={}",
                tablesSyncReqDTO.getTableNames(), success, System.currentTimeMillis() - start);
    }

    @Override
    public void deleteTable(TableSyncReqDTO tableSyncReqDTO) {
        long start = System.currentTimeMillis();
        String table = parseTableName(tableSyncReqDTO.getTableName());
        String index = table.toLowerCase(Locale.ROOT);
        log.info("ES 按表删除开始 table={}, index={}", table, index);
        esCommonService.deleteIndex(index);
        globalSearchSyncService.deleteByTable(table);
        log.info("ES 按表删除完成 table={}, index={}, costMs={}",
                table, index, System.currentTimeMillis() - start);
    }

    private String parseTableName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("参数缺少：表名");
        }
        String input = value.trim();
        int idx = input.indexOf('.');
        if (idx > 0 && idx < input.length() - 1) {
            return input.substring(idx + 1).trim();
        }
        return input;
    }

    private List<Map<String, Object>> queryRowsByMapper(String table) {
        return esDocumentConverter.toMapList(mapperTableQueryService.queryAllByTable(table));
    }
}
