package com.bio.drqi.es.support;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class MapperTableQueryService {

    private static final String MAPPER_PACKAGE = "com.bio.drqi.mapper";

    private final SqlSessionFactory sqlSessionFactory;
    private final ApplicationContext applicationContext;
    private final AtomicReference<Map<String, Class<?>>> tableMapperCache = new AtomicReference<>();

    public MapperTableQueryService(SqlSessionFactory sqlSessionFactory, ApplicationContext applicationContext) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.applicationContext = applicationContext;
    }

    public Collection<?> queryAllByTable(String table) {
        BaseMapper<?> baseMapper = getBaseMapper(table);
        return baseMapper.selectList(null);
    }

    public Object queryByTableAndId(String table, Object id) {
        if (id == null) {
            return null;
        }
        BaseMapper<?> baseMapper = getBaseMapper(table);
        if (id instanceof Serializable) {
            return baseMapper.selectById((Serializable) id);
        }
        return baseMapper.selectById(String.valueOf(id));
    }

    private BaseMapper<?> getBaseMapper(String table) {
        Class<?> mapperInterface = resolveMapperInterface(table);
        if (mapperInterface == null) {
            throw new IllegalStateException("未找到表对应Mapper: " + table);
        }
        Object bean = applicationContext.getBean(mapperInterface);
        if (!(bean instanceof BaseMapper)) {
            throw new IllegalStateException("Mapper不是BaseMapper实现: " + mapperInterface.getName());
        }
        return (BaseMapper<?>) bean;
    }

    private Class<?> resolveMapperInterface(String table) {
        String key = normalize(table);
        Map<String, Class<?>> mapping = tableMapperCache.get();
        if (mapping == null) {
            mapping = buildTableMapperMapping();
            tableMapperCache.compareAndSet(null, mapping);
            mapping = tableMapperCache.get();
        }
        return mapping.get(key);
    }

    private Map<String, Class<?>> buildTableMapperMapping() {
        Map<String, Class<?>> mapping = new HashMap<>();
        Collection<Class<?>> mappers = sqlSessionFactory.getConfiguration().getMapperRegistry().getMappers();
        for (Class<?> mapperInterface : mappers) {
            if (mapperInterface == null || mapperInterface.getName() == null) {
                continue;
            }
            if (!mapperInterface.getName().startsWith(MAPPER_PACKAGE + ".")) {
                continue;
            }
            Class<?> entityClass = resolveEntityClassFromMapper(mapperInterface);
            if (entityClass == null) {
                continue;
            }
            String tableName = resolveTableName(entityClass);
            if (tableName == null || tableName.trim().isEmpty()) {
                continue;
            }
            mapping.put(normalize(tableName), mapperInterface);
        }
        return Collections.unmodifiableMap(mapping);
    }

    private Class<?> resolveEntityClassFromMapper(Class<?> mapperInterface) {
        for (Type type : mapperInterface.getGenericInterfaces()) {
            Class<?> found = resolveEntityClassFromType(type);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private Class<?> resolveEntityClassFromType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class && BaseMapper.class.isAssignableFrom((Class<?>) rawType)) {
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                    return (Class<?>) typeArguments[0];
                }
            }
            if (rawType instanceof Class) {
                return resolveEntityClassFromMapper((Class<?>) rawType);
            }
        }
        if (type instanceof Class) {
            return resolveEntityClassFromMapper((Class<?>) type);
        }
        return null;
    }

    private String resolveTableName(Class<?> entityClass) {
        TableName tableName = entityClass.getAnnotation(TableName.class);
        if (tableName == null || tableName.value() == null || tableName.value().trim().isEmpty()) {
            return null;
        }
        return tableName.value().trim();
    }

    private String normalize(String table) {
        return table.trim().toLowerCase(Locale.ROOT);
    }
}
