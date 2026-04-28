package com.bio.drqi.es.service.impl;

import com.bio.drqi.es.dto.req.TableSyncReqDTO;
import com.bio.drqi.es.service.EsCommonService;
import com.bio.drqi.es.service.EsSyncService;
import com.bio.drqi.es.support.DomainEntityResolver;
import com.bio.drqi.es.support.EsDocumentConverter;
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
@ConditionalOnProperty(prefix = "sync.es", name = "enabled", havingValue = "true")
public class EsSyncServiceImpl implements EsSyncService {

    private static final String ID_FIELD = "id";

    private final EsCommonService esCommonService;
    private final DomainEntityResolver domainEntityResolver;
    private final EsMappingBuilder esMappingBuilder;
    private final EsDocumentConverter esDocumentConverter;
    private final MapperTableQueryService mapperTableQueryService;

    public EsSyncServiceImpl(EsCommonService esCommonService,
                             DomainEntityResolver domainEntityResolver,
                             EsMappingBuilder esMappingBuilder,
                             EsDocumentConverter esDocumentConverter,
                             MapperTableQueryService mapperTableQueryService) {
        this.esCommonService = esCommonService;
        this.domainEntityResolver = domainEntityResolver;
        this.esMappingBuilder = esMappingBuilder;
        this.esDocumentConverter = esDocumentConverter;
        this.mapperTableQueryService = mapperTableQueryService;
    }

    @Override
    public void syncTable(TableSyncReqDTO tableSyncReqDTO) {

        String table = parseTableName(tableSyncReqDTO.getTableName());
        String index = table.toLowerCase(Locale.ROOT);
        Class<?> entityClass = domainEntityResolver.resolveEntityClass(table);
        if (entityClass == null) {
            throw new IllegalStateException("在包 com.bio.drqi.domain 下找不到表对应实体: " + table);
        }
        Map<String, Object> mapping = esMappingBuilder.buildMappingByEntity(entityClass);
        int fieldCount = ((Map<?, ?>) mapping.get("properties")).size();
        if (fieldCount == 0) {
            throw new IllegalStateException("实体无可用字段: " + entityClass.getName());
        }
        esCommonService.recreateIndex(index, mapping);

        List<Map<String, Object>> rows = queryRowsByMapper(table);
        esCommonService.saveBatch(index, ID_FIELD, rows);


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
