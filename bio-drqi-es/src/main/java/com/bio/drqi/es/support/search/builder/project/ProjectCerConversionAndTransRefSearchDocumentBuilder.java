package com.bio.drqi.es.support.search.builder.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerConversionAndTransRef;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.mapper.BioSampleTestTbMapper;
import com.bio.drqi.mapper.CerConversionAndTransRefMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerConversionAndTransRefSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder<CerConversionAndTransRef> {

    private final CerConversionAndTransRefMapper cerConversionAndTransRefMapper;
    private final BioSampleTestTbMapper bioSampleTestTbMapper;
    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    public ProjectCerConversionAndTransRefSearchDocumentBuilder(CerConversionAndTransRefMapper cerConversionAndTransRefMapper,
                                                                BioSampleTestTbMapper bioSampleTestTbMapper,
                                                                CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.cerConversionAndTransRefMapper = cerConversionAndTransRefMapper;
        this.bioSampleTestTbMapper = bioSampleTestTbMapper;
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    @Override
    public String table() {
        return "cer_conversion_and_trans_ref";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        fillSampleInfo(row);
        String transGeneFlagName = transGeneFlagName(row.get("trans_gene_flag"));
        String sampleCode = stringValue(row.get("sample_code"));
        String transformCode = stringValue(row.get("transform_code"));
        String title = sampleCode.trim().isEmpty() ? transformCode : sampleCode;
        return buildDoc(row,
                title,
                join(row.get("project_code"), row.get("vector_task_code"), row.get("transform_code"), transGeneFlagName),
                "/project/conversion-trans-ref/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "实施方案编号", row.get("vector_task_code"), "取样编号", row.get("sample_code"), "转化编号", row.get("transform_code"), "创建人", row.get("create_user_name")),
                row.values(), transGeneFlagName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        fillSampleInfo(row);
        row.put("trans_gene_flag_name", transGeneFlagName(row.get("trans_gene_flag")));
        return row;
    }

    private void fillSampleInfo(Map<String, Object> row) {
        String vectorTaskCode = stringValue(row.get("vector_task_code"));
        if (vectorTaskCode.trim().isEmpty()) {
            return;
        }
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            return;
        }
        row.put("project_code", cerVectorTaskTb.getProjectCode());
        row.put("sub_project_code", cerVectorTaskTb.getSubProjectCode());
    }

    @Override
    public Class<CerConversionAndTransRef> entityClass() {
        return CerConversionAndTransRef.class;
    }

    @Override
    public BaseMapper<CerConversionAndTransRef> mapper() {
        return cerConversionAndTransRefMapper;
    }

}
