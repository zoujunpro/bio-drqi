package com.bio.drqi.es.support.search.project.biotest;

import com.bio.drqi.domain.BioSampleTestOneResultTb;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class ProjectBioSampleTestOneResultSearchDocumentBuilder extends AbstractBioTestSearchDocumentBuilder<BioSampleTestOneResultTb> {

    @Override
    public String table() {
        return "bio_sample_test_one_result_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("task_num"), row.get("upload_num"), row.get("test_user_name")),
                "/project/sample-test/one-result/detail/",
                display("取样编号", row.get("sample_code"), "任务编号", row.get("task_num"), "上传编号", row.get("upload_num"), "检测人", row.get("test_user_name")),
                row.values());
    }

    @Override
    public Map<String, Object> converter(BioSampleTestOneResultTb bioSampleTestOneResultTb) {
        return Collections.emptyMap();
    }


}
