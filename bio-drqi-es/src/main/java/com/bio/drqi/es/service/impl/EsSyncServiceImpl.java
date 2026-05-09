package com.bio.drqi.es.service.impl;

import com.bio.drqi.es.dto.req.TableSyncReqDTO;
import com.bio.drqi.es.dto.req.TablesSyncReqDTO;
import com.bio.drqi.es.enums.TableEnum;
import com.bio.drqi.es.service.EsCommonService;
import com.bio.drqi.es.service.EsSyncService;
import com.bio.drqi.es.support.EsDocumentConverter;
import com.bio.drqi.es.support.EsMappingBuilder;
import com.bio.drqi.es.support.search.GlobalSearchSyncService;
import com.bio.drqi.es.support.search.SearchDocumentBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class EsSyncServiceImpl implements EsSyncService {

    private static final String ID_FIELD = "id";

    private final Map<String, SearchDocumentBuilder> builderMap;

    private final EsCommonService esCommonService;
    private final EsMappingBuilder esMappingBuilder;
    private final EsDocumentConverter esDocumentConverter;
    private final GlobalSearchSyncService globalSearchSyncService;

    public EsSyncServiceImpl(EsCommonService esCommonService,
                             EsMappingBuilder esMappingBuilder,
                             EsDocumentConverter esDocumentConverter,
                             GlobalSearchSyncService globalSearchSyncService,
                             List<SearchDocumentBuilder> builders) {
        this.builderMap = builders == null ? Collections.emptyMap() : builders.stream().collect(Collectors.toMap(SearchDocumentBuilder::table, searchDocumentBuilder -> searchDocumentBuilder));
        this.esCommonService = esCommonService;
        this.esMappingBuilder = esMappingBuilder;
        this.esDocumentConverter = esDocumentConverter;
        this.globalSearchSyncService = globalSearchSyncService;
    }

    @Override
    public void syncTable(TableSyncReqDTO tableSyncReqDTO) {

        long start = System.currentTimeMillis();
        TableEnum tableEnum = TableEnum.getTableEnum(tableSyncReqDTO.getTableName().toLowerCase());
        log.info("ES 全量同步开始 table={}, index={}", tableEnum.name(), tableEnum.name());
        Class<?> entityClass = tableEnum.domain;
        Map<String, Object> mapping = esMappingBuilder.buildMappingByEntity(entityClass);
        int fieldCount = ((Map<?, ?>) mapping.get("properties")).size();
        if (fieldCount == 0) {
            throw new IllegalStateException("实体无可用字段: " + entityClass.getName());
        }
        log.info("ES 全量同步 mapping 构建完成 table={}, fieldCount={}", tableEnum.name(), fieldCount);
        esCommonService.recreateIndex(tableEnum.name(), mapping);
        globalSearchSyncService.deleteByTable(tableEnum.name());

        List<Map<String, Object>> rows = builderMap.get(tableEnum.name()).buildRows(null);
        log.info("ES 全量同步查询数据库完成 table={}, rows={}", tableEnum.name(), rows.size());
        esCommonService.saveBatch(tableEnum.name(), ID_FIELD, rows);
        globalSearchSyncService.saveBatch(tableEnum.name(), rows);
        log.info("ES 全量同步完成 table={}, index={}, rows={}, costMs={}",
                tableEnum.name(), tableEnum.name(), rows.size(), System.currentTimeMillis() - start);
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
        String table = TableEnum.getTableEnum(tableSyncReqDTO.getTableName().toLowerCase()).name();
        String index = table.toLowerCase(Locale.ROOT);
        log.info("ES 按表删除开始 table={}, index={}", table, index);
        esCommonService.deleteIndex(index);
        globalSearchSyncService.deleteByTable(table);
        log.info("ES 按表删除完成 table={}, index={}, costMs={}",
                table, index, System.currentTimeMillis() - start);
    }

}
