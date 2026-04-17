package com.bio.flow.print;

import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.flow.dto.BioHtmlModelDTO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TaskPrintTemplatePreviewTest {

    private static final String OUTPUT_FILE = "bio-drqi-flow/target/project-create-print-preview.html";

    @Test
    public void generatePreviewHtml() throws Exception {
        Configuration configuration = buildConfiguration();
        BioTaskDtlTb bioTaskDtlTb = buildTask();
        TestTaskPrintModelHandler handler = new TestTaskPrintModelHandler();
        BioHtmlModelDTO bioHtmlModelDTO = handler.handler(bioTaskDtlTb);
        Map<String, Object> model = buildTemplateModel(bioHtmlModelDTO);
        String html = render(configuration, "project_create_print.ftl", model);
        writeToFile(html);
    }

    private Configuration buildConfiguration() throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setClassLoaderForTemplateLoading(
                Thread.currentThread().getContextClassLoader(),
                "templates"
        );
        return configuration;
    }

    private BioTaskDtlTb buildTask() {
        BioTaskDtlTb bioTaskDtlTb = new BioTaskDtlTb();
        bioTaskDtlTb.setId(1);
        bioTaskDtlTb.setTaskNum("P202604170001");
        bioTaskDtlTb.setTaskTypeCode("project_add");
        bioTaskDtlTb.setTaskTypeName("项目新增");
        bioTaskDtlTb.setTaskDesc("ProjectAddDTO 风格打印模板预览案例，包含字段区块和计划明细表");
        bioTaskDtlTb.setApplyUserName("邹军");
        bioTaskDtlTb.setTaskStatus("2");
        bioTaskDtlTb.setRefTaskNum("");
        return bioTaskDtlTb;
    }

    private Map<String, Object> buildTemplateModel(BioHtmlModelDTO bioHtmlModelDTO) {
        Map<String, Object> result = new HashMap<>();
        result.put("modelHeader", bioHtmlModelDTO.getModelHeader());
        result.put("sections", bioHtmlModelDTO.getSections());
        result.put("modelBottomList", bioHtmlModelDTO.getModelBottomList());
        return result;
    }

    private String render(Configuration configuration, String templateName, Map<String, Object> model) throws Exception {
        Template template = configuration.getTemplate(templateName, "UTF-8");
        StringWriter stringWriter = new StringWriter();
        template.process(model, stringWriter);
        return stringWriter.toString();
    }

    private void writeToFile(String html) throws IOException {
        Path outputPath = Paths.get(OUTPUT_FILE);
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, html.getBytes(StandardCharsets.UTF_8));
    }
}
