package com.bio.drqi.ai.dao.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * AI DAO Mapper 扫描配置。
 */
@Configuration
@MapperScan("com.bio.drqi.ai.dao.mapper")
public class AiDaoMapperScanConfig {
}
