package com.bio.drqi.es.support;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class DomainEntityResolver {

    private static final String DOMAIN_PACKAGE = "com.bio.drqi.domain";
    private static final String DOMAIN_PATTERN = "classpath*:com/bio/drqi/domain/*.class";

    private final AtomicReference<List<Class<?>>> domainClassCache = new AtomicReference<>();

    public Class<?> resolveEntityClass(String table) {
        List<Class<?>> matched = new ArrayList<>();
        for (Class<?> domainClass : listDomainClasses()) {
            TableName tableName = domainClass.getAnnotation(TableName.class);
            if (tableName == null) {
                continue;
            }
            String value = tableName.value();
            if (value == null || value.trim().isEmpty()) {
                continue;
            }
            if (table.equalsIgnoreCase(value.trim())) {
                matched.add(domainClass);
            }
        }
        if (matched.isEmpty()) {
            return null;
        }
        if (matched.size() > 1) {
            throw new IllegalStateException("匹配到多个实体，请确认表名: " + table + "，匹配实体=" + matched);
        }
        return matched.get(0);
    }

    private List<Class<?>> listDomainClasses() {
        List<Class<?>> cached = domainClassCache.get();
        if (cached != null) {
            return cached;
        }
        synchronized (domainClassCache) {
            cached = domainClassCache.get();
            if (cached != null) {
                return cached;
            }
            List<Class<?>> classes = new ArrayList<>();
            try {
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
                Resource[] resources = resolver.getResources(DOMAIN_PATTERN);
                for (Resource resource : resources) {
                    String className = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
                    if (!className.startsWith(DOMAIN_PACKAGE)) {
                        continue;
                    }
                    classes.add(Class.forName(className));
                }
            } catch (Exception e) {
                throw new IllegalStateException("扫描 domain 实体失败", e);
            }
            domainClassCache.set(classes);
            return classes;
        }
    }
}

