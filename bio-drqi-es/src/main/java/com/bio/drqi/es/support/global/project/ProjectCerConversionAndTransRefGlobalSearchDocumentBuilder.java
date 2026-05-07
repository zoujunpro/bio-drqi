package com.bio.drqi.es.support.global.project;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerConversionAndTransRefGlobalSearchDocumentBuilder extends AbstractProjectGlobalSearchDocumentBuilder {

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
}
