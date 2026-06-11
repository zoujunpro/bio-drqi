package com.bio.drqi.es.support.search.builder.seed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.SeedStockOutLog;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.SeedStockOutLogMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SeedStockOutLogSearchDocumentBuilder extends AbstractSeedStockRecordSearchDocumentBuilder<SeedStockOutLog> {

    private final SeedStockOutLogMapper seedStockOutLogMapper;

    public SeedStockOutLogSearchDocumentBuilder(SeedStockOutLogMapper seedStockOutLogMapper,
                                                CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        super(cerVectorTaskTbMapper);
        this.seedStockOutLogMapper = seedStockOutLogMapper;
    }

    @Override
    public String table() {
        return "seed_stock_out_log";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        return buildSeedRecordDoc(row, "种子出库", "出库数量", "/seed/stockOut/detail/", row.get("out_task_num"), row.get("task_num"), row.get("use_to_code"), row.get("use_to_desc"), row.get("remarks"));
    }

    @Override
    public Class<SeedStockOutLog> entityClass() {
        return SeedStockOutLog.class;
    }

    @Override
    public BaseMapper<SeedStockOutLog> mapper() {
        return seedStockOutLogMapper;
    }
}
