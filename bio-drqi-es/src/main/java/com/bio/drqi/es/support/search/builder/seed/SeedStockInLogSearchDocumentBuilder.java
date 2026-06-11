package com.bio.drqi.es.support.search.builder.seed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.SeedStockInLog;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.SeedStockInLogMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SeedStockInLogSearchDocumentBuilder extends AbstractSeedStockRecordSearchDocumentBuilder<SeedStockInLog> {

    private final SeedStockInLogMapper seedStockInLogMapper;

    public SeedStockInLogSearchDocumentBuilder(SeedStockInLogMapper seedStockInLogMapper,
                                               CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        super(cerVectorTaskTbMapper);
        this.seedStockInLogMapper = seedStockInLogMapper;
    }

    @Override
    public String table() {
        return "seed_stock_in_log";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        return buildSeedRecordDoc(row, "种子入库", "入库数量", "/seed/stockIn/detail/", row.get("task_num"), row.get("remarks"));
    }

    @Override
    public Class<SeedStockInLog> entityClass() {
        return SeedStockInLog.class;
    }

    @Override
    public BaseMapper<SeedStockInLog> mapper() {
        return seedStockInLogMapper;
    }
}
