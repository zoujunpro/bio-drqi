package com.bio.drqi.es.support.search.project.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerConversionAndTransTb;
import com.bio.drqi.mapper.CerConversionAndTransTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerConversionAndTransSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder<CerConversionAndTransTb> {

    private final CerConversionAndTransTbMapper cerConversionAndTransTbMapper;

    public ProjectCerConversionAndTransSearchDocumentBuilder(CerConversionAndTransTbMapper cerConversionAndTransTbMapper) {
        this.cerConversionAndTransTbMapper = cerConversionAndTransTbMapper;
    }

    @Override
    public String table() {
        return "cer_conversion_and_trans_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        return buildDoc(row,
                stringValue(row.get("task_num")),
                join(row.get("handover_date"), row.get("create_user_name"), row.get("trans_number")),
                "/project/conversion-trans/detail/",
                display("任务编号", row.get("task_num"), "交接日期", row.get("handover_date"), "提交人", row.get("create_user_name"), "交接数量", row.get("trans_number")),
                row.values());
    }

    @Override
    public Class<CerConversionAndTransTb> entityClass() {
        return CerConversionAndTransTb.class;
    }

    @Override
    public BaseMapper<CerConversionAndTransTb> mapper() {
        return cerConversionAndTransTbMapper;
    }

}
