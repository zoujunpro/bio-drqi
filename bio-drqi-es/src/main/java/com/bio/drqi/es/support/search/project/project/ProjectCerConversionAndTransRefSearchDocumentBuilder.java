package com.bio.drqi.es.support.search.project.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerConversionAndTransRef;
import com.bio.drqi.mapper.CerConversionAndTransRefMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerConversionAndTransRefSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder<CerConversionAndTransRef> {

    private final CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    public ProjectCerConversionAndTransRefSearchDocumentBuilder(CerConversionAndTransRefMapper cerConversionAndTransRefMapper) {
        this.cerConversionAndTransRefMapper = cerConversionAndTransRefMapper;
    }

    @Override
    public String table() {
        return "cer_conversion_and_trans_ref";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String transGeneFlagName = transGeneFlagName(row.get("trans_gene_flag"));
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("project_code"), row.get("vector_task_code"), row.get("transform_code"), transGeneFlagName),
                "/project/conversion-trans-ref/detail/",
                display("取样编号", row.get("sample_code"), "项目编号", row.get("project_code"), "载体任务", row.get("vector_task_code"), "转化编号", row.get("transform_code"), "是否转基因", transGeneFlagName),
                row.values(), transGeneFlagName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("trans_gene_flag_name", transGeneFlagName(row.get("trans_gene_flag")));
        return row;
    }

    @Override
    protected BaseMapper<CerConversionAndTransRef> mapper() {
        return cerConversionAndTransRefMapper;
    }

}
