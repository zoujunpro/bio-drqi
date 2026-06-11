package com.bio.drqi.es.support.search.builder.seed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.SeedStockDestructionLog;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.SeedStockDestructionLogMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SeedStockDestructionLogSearchDocumentBuilder extends AbstractSeedStockRecordSearchDocumentBuilder<SeedStockDestructionLog> {

    private final SeedStockDestructionLogMapper seedStockDestructionLogMapper;

    public SeedStockDestructionLogSearchDocumentBuilder(SeedStockDestructionLogMapper seedStockDestructionLogMapper,
                                                        CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        super(cerVectorTaskTbMapper);
        this.seedStockDestructionLogMapper = seedStockDestructionLogMapper;
    }

    @Override
    public String table() {
        return "seed_stock_destruction_log";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        return buildSeedRecordDoc(row, "种子销毁", "销毁数量", "/seed/destruction/detail/", row.get("task_num"), row.get("destruction_method"), row.get("destruction_location"), row.get("remarks"));
    }

    @Override
    public Class<SeedStockDestructionLog> entityClass() {
        return SeedStockDestructionLog.class;
    }

    @Override
    public BaseMapper<SeedStockDestructionLog> mapper() {
        return seedStockDestructionLogMapper;
    }
}
